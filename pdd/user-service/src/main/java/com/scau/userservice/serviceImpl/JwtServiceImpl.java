package com.scau.userservice.serviceImpl;

import com.auth0.jwt.interfaces.Claim;
import com.scau.userservice.util.JwtUtil;
import com.scau.userservice.util.MD5Util;
import exception.ServiceException;
import model.Role;
import model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import service.JwtService;
import service.UserService;
import util.RedisUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: pdd
 * @description: 实现jwt相关功能，登录验证jwt
 * @create: 2020-11-15 17:24
 **/

@DubboService   //(version = "1.0.2",group = "lwb")
public class JwtServiceImpl implements JwtService {

    //jwt过期时间，单位小时
    private static int expireTime=60* 60 * 1000;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String login(User user) {
        //进行登录校验
        try{
            user.setPassword(MD5Util.getMD5Str(user.getPassword()));
            System.out.println(user);
            if(!StringUtils.isBlank(user.getUsername())){ //用户名登录
                User one=userService.getUserByUsername(user.getUsername());
                System.out.println(one);
                if(one==null)  throw new ServiceException("查询用户失败，不能存在用户");
                String jwt;
                if(one.getPassword().equals(user.getPassword())){ //密码正确
                    jwt=this.generateNewJwt(user.getUsername(),getMaxRole(one.getRoles()));
                    System.out.println(jwt);
                    return jwt;
                }else{
                    return null;
                }
            }else if(!StringUtils.isBlank(user.getEmail())){ //邮箱登录
                User one=userService.getUserByEmail(user.getEmail());
                if(one.getPassword().equals(user.getPassword())){ //密码正确
                    return this.generateNewJwt(user.getUsername(),getMaxRole(one.getRoles()));
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }catch (Exception e){
            throw new ServiceException("登录失败，jwt");
        }
    }

    /**
     * 获取当前账号最高权限
     * @param roles
     * @return
     */
    private String getMaxRole(List<Role> roles){
        AtomicReference<String> role=new AtomicReference<>();
        roles.forEach(e->{
            switch (e.getRoleid()){
                case "4":
                    role.set("admin");
                    break;
                case "3":
                    role.set("service");
                    break;
                case "2":
                    role.set("seller");
                    break;
                case "1":
                    role.set("user");
                    break;
            }
        });
        return role.toString();
    }

    private String getRoleFromSecret(String jwt,String secret){
        Map<String, Claim> map = JwtUtil.decode(jwt,secret);
        return map.get("role").asString();
    }

    private int compareRoleLevel(String role1,String role2){
        int r1,r2;
        switch (role1){
            case "admin":
                r1=4;
                break;
            case "service":
                r1=3;
                break;
            case "seller":
                r1=2;
                break;
            case "user":
                r1=1;
                break;
            default:r1=0;
        }
        switch (role2){
            case "admin":
                r2=4;
                break;
            case "service":
                r2=3;
                break;
            case "seller":
                r2=2;
                break;
            case "user":
                r2=1;
                break;
            default:r2=0;
        }

        return r1-r2;
    }

    @Override
    public String refreshJwt(String jwt) {
        String secret=(String) redisUtil.get(jwt);
        Map<String, Claim> map = JwtUtil.decode(jwt,secret);
        if(map.get("exp").asLong()*1000 - System.currentTimeMillis()/1000<30*60*1000){
            return this.generateNewJwt(map.get("name").asString(), map.get("role").asString());
        }else{
            return jwt;
        }
    }

    @Override
    public String generateNewJwt(String name,String role) {
        String secret = UUID.randomUUID().toString().replaceAll("-", "");
        String token = JwtUtil.encode(name,role, secret, expireTime);
        redisUtil.set(token,secret,expireTime);
        return token;
    }

    @Override
    public boolean checkJwt(String jwt,String role) {
            String secret = (String)redisUtil.get(jwt);
            if(StringUtils.isBlank(secret)) return false;
            String r=getRoleFromSecret(jwt,secret);
            if(compareRoleLevel(r,role)<0) return false;
            JwtUtil.decode(jwt, secret); //确认密钥配对
            return true;
    }

    @Override
    public boolean checkJwtToken(String jwt, String token,String url) {
        String secret = (String)redisUtil.get(jwt);
        if(StringUtils.isBlank(secret)) return false;
        Map<String, Claim> map = JwtUtil.decode(jwt,secret);
        if(map.get("token").asString().equals(token)&&map.get("url").asString().equals(url)) return true;
        else return false;
    }

    @Override
    public void inValid(String jwt) {
        redisUtil.del(jwt);
    }

    @Override
    public String addTokenToJwt(String jwt, String token,String url) {
        try{
            String sc = UUID.randomUUID().toString().replaceAll("-", "");
            String secret=(String) redisUtil.get(jwt);
            Map<String, Claim> map = JwtUtil.decode(jwt,secret);
            String jwt2=JwtUtil.encode(map,token,sc,url,expireTime);
            redisUtil.set(jwt2,sc,expireTime);
            inValid(jwt);
            return jwt2;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User getUserFromJwt(String jwt) {
        String secret = (String)redisUtil.get(jwt);
        Map<String, Claim> map = JwtUtil.decode(jwt,secret);
        String username=map.get("name").asString();
        User user=userService.getUserByUsername(username);
        System.out.println(user);
        return user;
    }


}
