package com.scau.chatservcie.config;

import model.Role;
import model.Shop;
import model.User;
import model.ZbInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Configuration;
import service.BroadcastServcie;
import service.UserService;

import java.util.List;

/**
 * @program: pdd
 * @description: 服务中心
 * @create: 2020-12-16 17:45
 **/
@Configuration
public class ServiceCenter {

    @DubboReference
    private UserService userService;

    @DubboReference
    private BroadcastServcie broadcastServcie;

    public UserService getUserService(){
        return this.userService;
    }

    public BroadcastServcie getBroadcastServcie() {
        return this.broadcastServcie;
    }
}