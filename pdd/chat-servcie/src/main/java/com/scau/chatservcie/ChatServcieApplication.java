package com.scau.chatservcie;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication(scanBasePackages = {"com.scau.chatservcie","util"},exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableDubbo
@Controller
public class ChatServcieApplication {

    @GetMapping("/")
    public String get1(){
        return "chat.html";
    }
    public static void main(String[] args) {
        SpringApplication.run(ChatServcieApplication.class, args);
    }

}
