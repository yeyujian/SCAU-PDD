package com.scau.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.Shop;
import model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * @program: pdd
 * @description: 用户管理数据库接口
 * @create: 2020-11-18 19:41
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from sys_user where username=#{username}")
    @Results(id="userMap",value = {
            @Result(id=true,column="id",property="id"),
            @Result(column="username",property="username"),
            @Result(column="password",property="password"),
            @Result(column="phone",property="phone"),
            @Result(column="sex",property="sex"),
            @Result(column="email",property="email"),
            @Result(column="nickname",property="nickname"),
            @Result(column="lastLogin",property="lastLogin"),
            @Result(column="loginIp",property="loginIp"),
            @Result(column="imageUrl",property="imageUrl"),
            @Result(column="regTime",property="regTime"),
            @Result(column="id",property="roles",
                    many=@Many(
                            select="com.scau.userservice.mapper.RoleMapper.getRolesByUserId",
                            fetchType= FetchType.LAZY
                    )
            )
    })
    User getUserByUsername(String username);

    @Select("select * from sys_user where id=#{userid}")
    @ResultMap(value = "userMap")
    User getUserByUserId(String userid);

    @Select("select * from sys_user where email=#{email}")
    @ResultMap(value = "userMap")
    User getUserByUserEmail(String email);

    @Select("select * from sys_user where id in (select toid from user_followed where fromid=#{userid})")
    List<User> getFollowsByUserid(String userid);

    @Insert("insert into user_followed (fromid,toid) values (#{fromid},#{toid})")
    int followedToUser(String fromid,String toid);

    @Insert("insert into pdd_shop_info (id,shop_name) values (#{fromid},#{toid})")
    int createShop(String id,String shopName);

    @Select("select * from pdd_shop_info where id=#{shopid}")
    Shop selectShopById(String shopid);
}
