package com.scau.userapp.controller;

import annotation.JwtCheck;
import entity.ResultEntity;
import model.Order;
import model.PddProductSku;
import model.Vo.OrderVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import service.GroupBuyService;
import service.PddGoodsService;
import service.PddOrderService;

import java.util.List;

/**
 * @program: pdd
 * @description: 商品控制中心
 * @create: 2020-12-17 20:52
 **/
@RestController
@RequestMapping("/shop")
public class ShopController {

//    @DubboReference
    private GroupBuyService groupBuyService;
//    @DubboReference
    private PddOrderService pddOrderService;
//    @DubboReference
    private PddGoodsService pddGoodsService;

    @GetMapping("/product/{productId}")
    public ResultEntity getProduct(@PathVariable("productId") String productId){
        PddProductSku productSku=pddGoodsService.getProductSkuByProductId(productId);
        return new ResultEntity.Builder().data(productSku).build();
    }

    @GetMapping("/order/{orderId}")
    @JwtCheck
    public ResultEntity getOrder(@PathVariable("orderId") String orderId){
        OrderVo orderVo=pddOrderService.getOrderById(orderId);
        return new ResultEntity.Builder().data(orderVo).build();
    }

    @PostMapping("/order")
    @JwtCheck
    public ResultEntity createOrder(OrderVo orderVo){
        OrderVo orderVo1=pddOrderService.createOrder(orderVo);
        return new ResultEntity.Builder().data(orderVo1).build();
    }


    @PostMapping("/group")
    @JwtCheck
    public ResultEntity startGroup(Order order){
        Order order1=groupBuyService.createGroup(order);
        return new ResultEntity.Builder().data(order1).build();
    }

    @GetMapping("/group/{productId}")
    @JwtCheck
    public ResultEntity getGroup(@PathVariable("productId") String productId){
        List<OrderVo> orderVo= groupBuyService.getGroup(productId);
        return new ResultEntity.Builder().data(orderVo).build();
    }

    @PutMapping("/group")
    @JwtCheck
    public ResultEntity addGroup(Order order,String groupOrderId){
        Order order1=groupBuyService.addGroup(order,groupOrderId);
        return new ResultEntity.Builder().data(order1).build();
    }
}
