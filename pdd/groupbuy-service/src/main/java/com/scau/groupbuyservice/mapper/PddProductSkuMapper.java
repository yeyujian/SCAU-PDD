package com.scau.groupbuyservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.PddProductSku;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

/**
 * @program: pdd
 * @description: 商品库存数据库接口
 * @create: 2020-12-07 20:15
 **/
@Mapper
public interface PddProductSkuMapper  extends BaseMapper<PddProductSku> {

    @Update("UPDATE pdd_product_sku SET stock = #{stock} WHERE product_id=#{productId} AND stock > 0")
    int updateStock(PddProductSku pddProductSku);

    @Insert("insert into pdd_product_sku " +
            "(id,product_id,product_name,price,promotion_price,stock,sold,shop_id)" +
            " values(#{id},#{productId},#{productName},#{price},#{promotionPrice},#{stock},#{sold},#{shop.id})")
    int insertProductSku(PddProductSku pddProductSku);


    @Select("select * from pdd_product_sku where product_id=#{productid} limit 0,1")
    @Results(id="userMap",value = {
            @Result(id=true,column="id",property="id"),
            @Result(column="product_id",property="productId"),
            @Result(column="product_name",property="productName"),
            @Result(column="price",property="price"),
            @Result(column="promotion_price",property="promotionPrice"),
            @Result(column="stock",property="stock"),
            @Result(column="sold",property="sold"),
            @Result(column="create_time",property="createTime"),
            @Result(column="shop_id",property="shop",
                    one=@One(
                            select="com.scau.groupbuyservice.mapper.ShopMapper.getShopByShopId",
                            fetchType= FetchType.LAZY
                    )
            )
    })
    PddProductSku selectPddProductSkuByProductId(String productid);
}
