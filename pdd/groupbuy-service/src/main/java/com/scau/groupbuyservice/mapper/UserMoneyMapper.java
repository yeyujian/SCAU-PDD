package com.scau.groupbuyservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.UserMoney;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * @program: pdd
 * @description: 用户钱包数据库接口
 * @create: 2020-12-07 20:13
 **/
@Mapper
public interface UserMoneyMapper extends BaseMapper<UserMoney> {

    @Update("update sys_user_money set money=money-#{money} where user_id=#{userid}")
    int updateUserMoneyByUserId(@Param("userid") String userid,@Param("money") BigDecimal money);
}
