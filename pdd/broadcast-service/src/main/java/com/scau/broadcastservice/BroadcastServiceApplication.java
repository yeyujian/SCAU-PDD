package com.scau.broadcastservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.scau.broadcastservice","util"},exclude= {DataSourceAutoConfiguration.class})
@EnableDubbo
public class BroadcastServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BroadcastServiceApplication.class, args);
    }

}
