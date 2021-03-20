package com.scau.dubbocustomer.controller;

import annotation.JwtCheck;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import service.MailService;

/**
 * @program: pdd
 * @description:
 * @create: 2020-11-15 22:14
 **/

@RestController
public class TestController {


    @GetMapping("/test/{p}")
//    @JwtCheck
    public String test(@PathVariable("p") String param) {
//        mailService.sendMail("2783880381@qq.com","hello",param);
        return param;
    }
}
