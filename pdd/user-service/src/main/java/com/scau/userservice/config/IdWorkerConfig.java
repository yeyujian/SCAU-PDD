package com.scau.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.IdWorker;

/**
 * @program: pdd
 * @description: 配置分布式id生成器
 * @create: 2020-11-26 19:41
 **/
@Configuration
public class IdWorkerConfig {

    @Bean
    IdWorker createIdWorkerConfig(){
        return new IdWorker(1,1);
    }
}
