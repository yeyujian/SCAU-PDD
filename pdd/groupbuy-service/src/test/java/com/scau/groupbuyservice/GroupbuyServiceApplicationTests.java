package com.scau.groupbuyservice;

import com.scau.groupbuyservice.mapper.OrderMapper;
import com.scau.groupbuyservice.mapper.OrderProductMapper;
import com.scau.groupbuyservice.mapper.UserMoneyMapper;
import com.scau.groupbuyservice.serviceImpl.PddGoodsServiceImpl;
import model.Order;
import model.OrderShipping;
import model.PddOrderProduct;
import model.Vo.OrderVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import service.PddGoodsService;
import service.PddOrderService;
import util.RedisUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class GroupbuyServiceApplicationTests {

    @Resource
    private PddGoodsService pddGoodsService;
    @Resource
    private PddOrderService pddOrderService;
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMoneyMapper userMoneyMapper;

    @Resource
    private OrderProductMapper orderProductMapper;
    @Test
    void contextLoads() {
        System.out.println(pddGoodsService.decremeterProductSku("1",1));
        System.out.println(redisUtil.get("product::1"));
        System.out.println(pddGoodsService.decremeterProductSku("1",1));
        System.out.println(redisUtil.get("product::1"));
//         DefaultRedisScript<Boolean> createProductSkuScript;
//        createProductSkuScript = new DefaultRedisScript<Boolean>();
//        createProductSkuScript.setResultType(Boolean.class);
//        createProductSkuScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/createProductSku.lua")));
//        List<String> keyList = new ArrayList();
//        keyList.add("123456");
//        redisUtil.exec(createProductSkuScript,keyList,5);
    }

    @Test
    void test2(){
        OrderVo orderVo=new OrderVo();
        orderVo.setOrderId("123");
        PddOrderProduct ps=new PddOrderProduct();
        ps.setId("1");
        ps.setOrderId("123");
        ps.setProductId("456");
        ps.setNum(3);
        orderVo.addOrderProduct(ps);
        ps=new PddOrderProduct();
        ps.setId("2");
        ps.setOrderId("123");
        ps.setProductId("hh");
        ps.setNum(7);
        orderVo.addOrderProduct(ps);
        OrderShipping orderShipping=new OrderShipping();
        orderShipping.setOrderId(orderVo.getOrderId());
        orderShipping.setReceiverCity("广州");
        orderShipping.setReceiverMobile("123456");
        orderShipping.setReceiverName("yyj");
        //orderShipping.setReceiverAddress("哈哈哈哈");
        orderVo.setOrderShipping(orderShipping);
        pddOrderService.insertOrder(orderVo);
//        orderMapper.insert(orderVo);
//        System.out.println(pddGoodsService.decremeterProductSkuList(orderVo.getProducts()));
//        System.out.println(redisUtil.get("product::"+ps.getProductId()));
    }

    @Test
    void test3(){
        PddOrderProduct pop=orderProductMapper.selectById("f464b");
        System.out.println(pop);
        pop.setId("9999999");
        List<PddOrderProduct> list=new LinkedList<>();
        list.add(pop);
//        list.add(pop);
        pddOrderService.insertOrderProduct(list);
    }


    @Test
    void test4(){
        userMoneyMapper.updateUserMoneyByUserId("1",new BigDecimal("100"));
    }
}
