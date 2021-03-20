package com.scau.userservice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.scau.userservice.mapper.UserMapper;
import com.scau.userservice.util.MD5Util;
import model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import service.JwtService;
import util.RedisUtil;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class UserServiceApplicationTests {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private JwtService jwtService;
    @Test
    void contextLoads()   {


        User user=new User();
        user.setEmail("1");
        user.setUsername("1");
        System.out.println(userMapper.getUserByUsername("1"));
        try{
            user.setPassword("4");
            String jwt=jwtService.login(user);
            System.out.println(jwt);
            System.out.println(jwtService.checkJwt(jwt,"user"));
//            jwt=jwtService.addTokenToJwt(jwt,"hello我爱你啊啊");
            System.out.println(jwt);
             System.out.println(jwtService.checkJwt(jwt,"user"));
//            System.out.println(jwtService.checkJwt(jwt2,"user"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
