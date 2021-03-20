package com.scau.userservice.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.scau.userservice.mapper.RoleMapper;
import com.scau.userservice.mapper.UserMapper;
import com.scau.userservice.mapper.UserRoleMapper;
import com.scau.userservice.util.MD5Util;
import model.Role;
import model.Shop;
import model.User;
import model.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.UserService;
import util.IdWorker;
import util.RedisUtil;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: pdd
 * @description: 用户管理服务
 * @create: 2020-11-18 19:38
 **/
@DubboService
public class UserServiceImpl implements UserService {

    @Autowired
    private IdWorker idWorker;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public User getUserByUsername(String username) {
        //设置redis空缓存 防止缓存穿透
        if(redisUtil.hasKey("isNullValue:"+username)) return null;
        User user= userMapper.getUserByUsername(username);
        System.out.println(user);
        if(user==null){
            redisUtil.set("isNullValue:"+username,1,5*60 * 1000); //设置5分钟的空缓存
        }
        return user;
    }

    @Override
    public List<Role> getRoles(String username) {
        return roleMapper.getRolesByUserName(username);
    }

    @Override
    public boolean addRole(Role role) {
        role.setRoleid(idWorker.nextId());
        return roleMapper.insert(role)>0;
    }

    @CacheEvict(value = "user", key= "#userid") //删除缓存
    @Override
    public boolean addRoleForUser(String roleid, String userid) {
        return userRoleMapper.insert(new UserRole(userid,roleid))>0;
    }

    @Override
    public boolean deleteRole(String id) {
        return roleMapper.deleteById(id)>0;
    }

    @Transactional
    @Override
    public boolean addUser(User user) throws Exception {
        user.setId(idWorker.nextId());
        user.setPassword(MD5Util.getMD5Str(user.getPassword()));
        userMapper.insert(user);
        return addRoleForUser("1",user.getId());
    }

    @Cacheable(value = "user", key= "#id")
    @Override
    public User getUserById(String id){
        return userMapper.selectById(id);
    }


    @Override
    public User getUserByPhone(String phone) {
        QueryWrapper<User> su = new QueryWrapper<>();
        su.eq("phone",phone);
        return userMapper.selectOne(su);
    }

    @Override
    public User getUserByEmail(String emaill) {
        return userMapper.getUserByUserEmail(emaill);
    }

    @CachePut(value = "user", key= "#user.id") //更新缓存
    @Override
    public User updateUser(User user) {
        UpdateWrapper<User> su = new UpdateWrapper<>();
        su.eq("username",user.getUsername());
        user.setId(null);
        user.setPassword(null);
        if(userMapper.update(user,su)<=0) return null;
        return userMapper.selectById(user.getId());
    }

    @CacheEvict(value = "user", key= "#user.id") //删除缓存
    @Override
    public boolean changePassword(User user)  {
        try{
            LambdaUpdateChainWrapper<User> lambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(userMapper);
            return lambdaUpdateChainWrapper.eq(User::getUsername, user.getUsername()).set(User::getPassword, MD5Util.getMD5Str(user.getPassword())).update();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("修改密码失败");
        }
    }

    @Cacheable(value = "follow", key= "#userid")
    @Override
    public List<User> getFollowsByUserid(String userid) {
        return userMapper.getFollowsByUserid(userid);
    }

    @CacheEvict(value = "follow", key= "#userid") //删除缓存
    @Override
    public boolean followedToSeller(String userid,String sellerid) {
        User user=getUserById(userid);
        if(!checkRole(user.getRoles(),"seller")) return false;
        return userMapper.followedToUser(userid,sellerid)>0;
    }


    /**
     * 检查是否有对应权限
     * @param roles
     * @return
     */
    private boolean checkRole(List<Role> roles,String role){
        Iterator<Role> iter = roles.iterator();
        while (iter.hasNext()) {
            Role e = (Role) iter.next();
            if(e.getRole().equals(role)) return true;
        }
        return false;
    }

    @Override
    public boolean createShop(Shop shop) {
         return userMapper.createShop(shop.getId(),shop.getShopName())>0;
    }

    @Override
    public Shop getShopById(String id) {
        return userMapper.selectShopById(id);
    }
}
