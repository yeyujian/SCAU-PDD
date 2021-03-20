package com.scau.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: pdd
 * @description: 角色管理数据库接口
 * @create: 2020-11-18 19:45
 **/
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("select r.* from sys_role r LEFT JOIN sys_user_role ur on ur.roleid=r.roleid where userid=#{uid}")
    List<Role> getRolesByUserId(String uid);

    @Select("select r.* from sys_role r LEFT JOIN sys_user_role ur on ur.roleid=r.roleid where userid in (select id from sys_user where id=#{username})")
    List<Role> getRolesByUserName(String username);
}
