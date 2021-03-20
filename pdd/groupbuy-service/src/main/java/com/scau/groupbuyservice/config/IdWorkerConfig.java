package com.scau.groupbuyservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.IdWorker;

/**
 * @program: pdd
 * @description: idworker配置文件
 * @create: 2020-12-07 22:46
 **/
@Configuration
public class IdWorkerConfig {

    @Bean
    IdWorker createIdWorkerConfig(){
        return new IdWorker(1,1);
    }
}
