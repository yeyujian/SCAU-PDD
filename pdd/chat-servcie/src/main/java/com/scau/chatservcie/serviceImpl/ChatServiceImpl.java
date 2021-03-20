package com.scau.chatservcie.serviceImpl;

import com.scau.chatservcie.config.rabbitmq.RabbitMQConfig;
import com.scau.chatservcie.util.BloomFilterUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import service.ChatService;
import util.RedisUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @program: pdd
 * @description: 聊天管理服务
 * @create: 2020-12-03 20:02
 **/
//@DubboService
public class ChatServiceImpl implements ChatService {

//    @Resource
    private RedisUtil redisUtil;

//    @Autowired
    private BloomFilterUtil bloomFilterUtil;


//    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean createZbChat(String token) {
        return redisUtil.hset(token+"::ZBchat",token,1);// 加入直播间;
    }

    @Override
    public boolean removeZbChat(String token) {
         redisUtil.del(token+"::ZBchat");
         return true;
    }

    @Override
    public boolean sendNotice(String username, String message) {
        //验证user是否存在，使用布隆过滤器检测身份是否存在
        if(!bloomFilterUtil.includeByBloomFilter("user",username+"::"+"user")){
            throw new RuntimeException("身份不匹配，非法数据");
        }
        CorrelationData CorrData = new CorrelationData(UUID.randomUUID().toString());
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("username",username);
        msgMap.put("messge",message);
        msgMap.put("id",UUID.randomUUID().toString()); //保证幂等
        rabbitTemplate.convertAndSend("NoticeExchange", RabbitMQConfig.NOTICE_USER,msgMap,CorrData);
        return false;
    }
}
