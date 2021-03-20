package com.scau.groupbuyservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.PddOrderProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: pdd
 * @description: 订单商品数据库接口
 * @create: 2020-12-07 20:17
 **/
@Mapper
public interface OrderProductMapper  extends BaseMapper<PddOrderProduct> {

    @Select("select * from pdd_order_product where order_id=#{id}")
    List<PddOrderProduct> selectOrderProductsByOrderId(String id);
}
