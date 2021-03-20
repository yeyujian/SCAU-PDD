package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

    int a=0;

    @RequestMapping("/")
    public String test(){
        long cur = 0;
        try {
            cur=System.currentTimeMillis();
            a++;
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }finally {
            return    String.valueOf(cur)+" ====="+ String.valueOf(System.currentTimeMillis())+ " :".concat(String.valueOf(a));
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
