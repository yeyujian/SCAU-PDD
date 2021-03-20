package com.scau.groupbuyservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.Vo.OrderVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

/**
 * @program: pdd
 * @description: 订单数据库接口
 * @create: 2020-12-07 20:16
 **/
@Mapper
public interface OrderMapper extends BaseMapper<OrderVo> {

    /**
     *     private String orderId;
     *     private BigDecimal payment;
     *     private int payType;
     *     private int status;
     *     private Date createTime;
     *     private Date updateTime;
     *     private Date paymentTime;
     *     private Date consignTime;
     *     private Date endTime;
     *     private Date closeTime;
     *     private String userId;
     */

    @Select("select * from sys_user where username=#{username}")
    @Results(id="userMap",value = {
            @Result(id=true,column="order_id",property="orderId"),
            @Result(column="payment",property="payment"),
            @Result(column="pay_type",property="payType"),
            @Result(column="status",property="status"),
            @Result(column="create_time",property="createTime"),
            @Result(column="update_time",property="updateTime"),
            @Result(column="payment_time",property="paymentTime"),
            @Result(column="consign_time",property="consignTime"),
            @Result(column="end_time",property="endTime"),
            @Result(column="close_time",property="closeTime"),
            @Result(column="user_id",property="userId"),
            @Result(column="order_id",property="products",
                    many=@Many(
                            select="com.scau.groupbuyservice.mapper.OrderProductMapper.selectOrderProductsByOrderId",
                            fetchType= FetchType.LAZY
                    )
            )
    })
    OrderVo selectOrderVoByOrderId(String id);

    @Insert("insert into ")
    int insertByOrderVo(OrderVo orderVo);
}
