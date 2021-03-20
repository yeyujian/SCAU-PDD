package com.scau.groupbuyservice.config.rabbitmq;

import com.rabbitmq.client.Channel;
import model.Vo.OrderVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import service.PddOrderService;
import util.RedisUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @program: pdd
 * @description: 订单消息处理
 * @create: 2020-12-10 20:11
 **/
@Component
public class OrderMessageReceiver {

    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private PddOrderService pddOrderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_INSERT)
    public void orderInsertProcess(Map<String, Object> msg, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try{
            OrderVo orderVo=(OrderVo)msg.get("orderVo");
            pddOrderService.insertOrder(orderVo);
            ch.basicAck(tag, false);// 消费确认
        }catch (Exception e){
            ch.basicNack(tag, false, true);
        }
    }
}
