package com.scau.groupbuyservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @program: pdd
 * @description: 商铺数据库接口
 * @create: 2020-12-07 20:15
 **/
@Mapper
public interface ShopMapper  extends BaseMapper<Shop> {

    @Select("select * from pdd_shop_info where id=#{shopid} limit 0,1")
    Shop getShopByShopId(String shopid);
}
