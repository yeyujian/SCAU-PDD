package com.scau.groupbuyservice.serviceImpl;

import com.scau.groupbuyservice.config.rabbitmq.RabbitMQConfig;
import model.Order;
import model.PddOrderProduct;
import model.UserMoney;
import model.Vo.OrderVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import service.GroupBuyService;
import service.PddGoodsService;
import service.PddOrderService;
import service.PddPayService;
import util.RedisUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * @program: pdd
 * @description: 拼团服务
 * @create: 2020-12-07 20:50
 **/
@DubboService
public class GroupBuyServiceImpl implements GroupBuyService {

    private static   Long TIMEOUT_SECOUND = 60000L;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PddPayService pddPayService;
    @Autowired
    private PddGoodsService pddGoodsService;
    @Autowired
    private PddOrderService pddOrderService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 1,检查订单是否存在  2，检查是否未支付
     * @param order
     * @return
     */
    @Transactional
    @Override
    public Order createGroup(Order order) {
        //对用户付款加锁 保证一致性
        while(!redisUtil.setnx("lock-pay-"+order.getUserId(),String.valueOf(System.currentTimeMillis()),1)){
                Long lockTime = Long.valueOf(((String)redisUtil.get("lock-pay-"+order.getUserId())).substring(9));
                if (lockTime!=null && System.currentTimeMillis() > lockTime+TIMEOUT_SECOUND) {
                    redisUtil.del("lock-pay-"+order.getUserId());
                }
        }
        OrderVo orderVo=(OrderVo)redisUtil.get("orderVo::"+order.getOrderId());
        if(orderVo==null) return null;
        if(orderVo.getStatus()!=1){
            redisUtil.del("orderVo::"+order.getOrderId());
            return null;
        }
        //付款 算出总金额
        List<UserMoney> toUserList=new ArrayList<>();
        UserMoney toUser=null;
        //付款
        UserMoney fromUser=pddPayService.getMoneyByUserid(orderVo.getUserId());
        //因为拼单的时候只有一个商品 直接存储list中的productid   这里没有处理退款
        String proecutid=null;
        for(PddOrderProduct product:orderVo.getProducts()){
            proecutid=product.getProductId();
            //扣钱
            fromUser.setMoney(product.getTotalFee());
            pddPayService.updateMoney(fromUser);
            //加钱
            toUser=pddPayService.getMoneyByUserid(pddGoodsService.getProductSkuByProductId(product.getProductId()).getShop().getId());
            BigDecimal money=product.getTotalFee().multiply(new BigDecimal("-1.00"));
            money.setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
            toUser.setMoney(money);
            pddPayService.updateMoney(toUser);
        }
        redisUtil.del("lock-pay-"+order.getUserId()); //解锁
        //修改订单写入redis
        if(!pddOrderService.updateOrderStatusForRedis(order.getOrderId(),2)){
            throw new RuntimeException("修改redis订单失败");
        }
        //开团拼单写入redis
        //用key: PT:{productid} 存储拼单列表  每次取一个出来 然后重新放入队列
        redisUtil.lSet("PT:"+proecutid,orderVo.getOrderId());
        //发送写入订单请求mq
        CorrelationData CorrData = new CorrelationData(UUID.randomUUID().toString());
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("orderVo",orderVo);
        rabbitTemplate.convertAndSend("OrderExchange", RabbitMQConfig.ORDER_INSERT, msgMap,CorrData);
        //发送减库存mq
        CorrData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("SkuExchange", RabbitMQConfig.SKU_DECR, msgMap,CorrData);
        return orderVo;
    }


    @Transactional
    @Override
    public Order addGroup(Order order,String pdOrderId) {
        //有些小逻辑错误 时间问题不处理了
        String proecutid=null;
        OrderVo orderVo=(OrderVo)redisUtil.get("orderVo::"+order.getOrderId());
        if(orderVo==null) return null;
        //对拼团订单锁定 防止多次拼团
        if(!redisUtil.setnx("PT:OK:"+pdOrderId,1)) return null;
        try{
            //对用户付款加锁 保证一致性
            while(!redisUtil.setnx("lock-pay-"+order.getUserId(),String.valueOf(System.currentTimeMillis()),1)){
                Long lockTime = Long.valueOf(((String)redisUtil.get("lock-pay-"+order.getUserId())).substring(9));
                if (lockTime!=null && System.currentTimeMillis() > lockTime+TIMEOUT_SECOUND) {
                    redisUtil.del("lock-pay-"+order.getUserId());
                }
            }
            if(orderVo.getStatus()!=1){
                redisUtil.del("lock-pay-"+order.getUserId());
                return null;
            }
            //付款 算出总金额
            List<UserMoney> toUserList=new ArrayList<>();
            UserMoney toUser=null;
            //付款
            UserMoney fromUser=pddPayService.getMoneyByUserid(orderVo.getUserId());
            //因为拼单的时候只有一个商品 直接存储list中的productid   这里没有处理退款
            for(PddOrderProduct product:orderVo.getProducts()){
                proecutid=product.getProductId();
                //扣钱
                fromUser.setMoney(product.getTotalFee());
                pddPayService.updateMoney(fromUser);
                //加钱
                toUser=pddPayService.getMoneyByUserid(pddGoodsService.getProductSkuByProductId(product.getProductId()).getShop().getId());
                BigDecimal money=product.getTotalFee().multiply(new BigDecimal("-1.00"));
                money.setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
                toUser.setMoney(money);
                pddPayService.updateMoney(toUser);
            }
            redisUtil.del("orderVo::"+order.getOrderId()); //解锁
            //修改订单写入redis
            if(!pddOrderService.updateOrderStatusForRedis(order.getOrderId(),2)){
                throw new RuntimeException("修改redis订单失败");
            }
            //删除开团的拼单
            redisUtil.lRemove("PT:"+proecutid,1,orderVo.getOrderId());
            //发送写入订单请求mq
            CorrelationData CorrData = new CorrelationData(UUID.randomUUID().toString());
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("orderVo",orderVo);
            rabbitTemplate.convertAndSend("OrderExchange", RabbitMQConfig.ORDER_INSERT, msgMap,CorrData);
            //发送减库存mq
            CorrData = new CorrelationData(UUID.randomUUID().toString());
            rabbitTemplate.convertAndSend("SkuExchange", RabbitMQConfig.SKU_DECR, msgMap,CorrData);
            return orderVo;
        }catch (Exception e){
            redisUtil.del("PT:OK:"+pdOrderId);
            //再次加入防止拼团消失
            redisUtil.lSet("PT:"+proecutid,orderVo.getOrderId());
            throw  new RuntimeException("加入拼团失败");
        }
    }


    /**
     *取出拼单  发送   设置拼单flag
     * @param goodsId
     * @return
     */
    @Override
    public List<OrderVo> getGroup(String goodsId) {
        List<OrderVo> ordersList=new ArrayList<>();
        String orderid=(String)redisUtil.lpop("PT:"+goodsId);
        System.out.println(orderid);
        if(orderid==null) return null;
        //检查此订单是否已经拼单成功，如果已经成功不再放入队列 并且删除对应的已经消费标志  如果订单还未拼单成功 可以定时1分钟后放入队列 这里就直接放入队列了
        OrderVo orderVo=(OrderVo) redisUtil.get("orderVo::"+orderid);
        if(orderVo==null) return null;
        if(redisUtil.hasKey("PT:OK:"+orderid)) {  //如果已经在支付
            redisUtil.del("PT:OK:"+orderid);
            return null;
        }
        //简单处理订单防止信息泄露
        orderVo.setOrderShipping(null);
        ordersList.add(orderVo);
        redisUtil.lSet("PT:"+goodsId,orderid);
        return ordersList;
    }
}
