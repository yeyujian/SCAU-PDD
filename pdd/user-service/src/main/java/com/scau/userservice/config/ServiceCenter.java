package com.scau.userservice.config;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Configuration;
import service.UserService;

/**
 * @program: pdd
 * @description: 服务中心
 * @create: 2020-12-16 17:45
 **/
//@Configuration
public class ServiceCenter {
//    @DubboReference
    private UserService userService;

    private UserService getUserService(){
        return this.userService;
    }
}
