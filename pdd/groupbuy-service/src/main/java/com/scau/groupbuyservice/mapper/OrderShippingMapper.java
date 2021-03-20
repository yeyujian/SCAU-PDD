package com.scau.groupbuyservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.OrderShipping;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: pdd
 * @description: 订单地址数据库接口
 * @create: 2020-12-07 20:12
 **/
@Mapper
public interface OrderShippingMapper extends BaseMapper<OrderShipping> {
}
