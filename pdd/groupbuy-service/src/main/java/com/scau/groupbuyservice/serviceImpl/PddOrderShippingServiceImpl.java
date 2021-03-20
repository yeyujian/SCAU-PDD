package com.scau.groupbuyservice.serviceImpl;

import com.scau.groupbuyservice.mapper.OrderShippingMapper;
import model.OrderShipping;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import service.PddOrderShippingService;

/**
 * @program: pdd
 * @description: 订单地址服务
 * @create: 2020-12-07 20:52
 **/
@DubboService
public class PddOrderShippingServiceImpl implements PddOrderShippingService {

    @Autowired
    private OrderShippingMapper orderShippingMapper;

    @Override
    public boolean insertShipping(OrderShipping orderShipping) {
        return orderShippingMapper.insert(orderShipping)>0;
    }

    @Override
    public boolean deleteShipping(String orderid) {
        return orderShippingMapper.deleteById(orderid)>0;
    }

    @Override
    public OrderShipping getShippingById(String orderid) {
        return orderShippingMapper.selectById(orderid);
    }
}
