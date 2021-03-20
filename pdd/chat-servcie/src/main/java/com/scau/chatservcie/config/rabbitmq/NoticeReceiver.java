package com.scau.chatservcie.config.rabbitmq;



import com.rabbitmq.client.Channel;
import com.scau.chatservcie.entity.UserChannelMap;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import util.RedisUtil;

import java.io.IOException;
import java.util.Map;

/**
 * @program: pdd
 * @description: 接受消息队列信息进行处理
 * @create: 2020-12-04 10:43
 **/
@Component
public class NoticeReceiver {

    @Autowired
    private RedisUtil redisUtil;

    @RabbitListener(queues = RabbitMQConfig.NOTICE_USER)
    public void friendRequireProcess(Map<String, Object> msg, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try{
            String username=(String)msg.get("username");
            String message=(String)msg.get("message");
            String id=(String)msg.get("id");
            if(redisUtil.get(id)!=null) {
                redisUtil.del(id);
                ch.basicAck(tag, false);// 消费确认
                return;
            }
            io.netty.channel.Channel channel = UserChannelMap.get(username+"::user");
            if(channel==null){
                redisUtil.lSet("UnReadMsg:user:"+username,message);
            }else{
                channel.writeAndFlush(new TextWebSocketFrame(message)); // 直接发送请求
            }

            ch.basicNack(tag, false, true);
            redisUtil.set(id,1); //幂等
            ch.basicAck(tag, false);// 消费确认
        }catch (Exception e){
            ch.basicNack(tag, false, true);
        }
    }
}
