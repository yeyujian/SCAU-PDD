package com.scau.groupbuyservice.serviceImpl;

import com.scau.groupbuyservice.mapper.OrderMapper;
import com.scau.groupbuyservice.mapper.OrderProductMapper;
import model.PddOrderProduct;
import model.PddProductSku;
import model.Vo.OrderVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import service.PddGoodsService;
import service.PddOrderService;
import service.PddOrderShippingService;
import util.IdWorker;
import util.RedisUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: pdd
 * @description: 订单管理服务
 * @create: 2020-12-07 20:50
 **/
@DubboService
public class PddOrderServiceImpl implements PddOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderProductMapper orderProductMapper;
    @Autowired
    private PddOrderShippingService pddOrderShippingService;
    @Autowired
    private PddGoodsService pddGoodsService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public OrderVo getOrderById(String id) {
        OrderVo ordeVo=(OrderVo)redisUtil.get("orderVo::"+id);
        if(ordeVo!=null) return ordeVo;
        return orderMapper.selectOrderVoByOrderId(id);
    }

    /**
     * 1,检查库存是否 2,预减库存使用lua脚本保证原子性 3，获取对应商品属性
     * 创建订单 订单缓存到redis
     * @param orderVo
     * @return
     */
    @Override
    public OrderVo createOrder(OrderVo orderVo) {
        //这里还需处理订单超时未付款  时间关系 不写了
        orderVo.setOrderId(idWorker.nextId());
        orderVo.setCreateTime(new Date());
        orderVo.setStatus(1);
        //orderVo.setCloseTime(new Date(orderVo.getCreateTime().getTime()+600000));
        PddProductSku pps=null;
        /**
         * BigDecimal 运算
         * add() 加法
         * subtract() 减法
         * multiply() 乘法
         * divide() 除法
         * setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue()
         */
        //获取单价  计算单商品总金额
        for(PddOrderProduct product:orderVo.getProducts()){
            pps=pddGoodsService.getProductSkuByProductId(product.getProductId());
            product.setOrderId(orderVo.getOrderId());
            BigDecimal price=pps.getPromotionPrice();
            product.setPrice(price); //单价
            BigDecimal total=price.multiply(new BigDecimal(product.getNum()));
            total.setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
            product.setTotalFee(total);  //总金额
        }
        if(!pddGoodsService.decremeterProductSkuList(orderVo.getProducts())) return null;
        redisUtil.setnx("orderVo::"+orderVo.getOrderId(),orderVo);
        return orderVo;
    }

    /**
     * 将订单插入数据库
     * @param orderVo
     * @return
     */
    @Transactional
    @Override
    public boolean insertOrder(OrderVo orderVo) {
        orderMapper.insert(orderVo);
        insertOrderProduct(orderVo.getProducts());
        pddOrderShippingService.insertShipping(orderVo.getOrderShipping());
        return true;
    }

    @Override
    public boolean updateOrderStatusForRedis(String oriderid, int status) {
        OrderVo orderVo=getOrderById(oriderid);
        if(orderVo.getStatus()==status) return false;
        orderVo.setStatus(status);
        redisUtil.set("orderVo::"+oriderid,orderVo);
        return true;
    }

    @Transactional
    @Override
    public boolean insertOrderProduct(List<PddOrderProduct> products) {
        for(PddOrderProduct product:products){
            orderProductMapper.insert(product);
        }
        return true;
    }
}
