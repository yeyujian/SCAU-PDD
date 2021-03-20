package com.scau.groupbuyservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.scau.groupbuyservice","util"})
@EnableDubbo
@EnableCaching // 开启缓存
@MapperScan("com.scau.groupbuyservice.mapper")
public class GroupbuyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupbuyServiceApplication.class, args);
    }

}
