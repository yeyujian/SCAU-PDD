package com.scau.groupbuyservice.config.rabbitmq;

import com.rabbitmq.client.Channel;
;
import model.Vo.OrderVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import service.PddGoodsService;


import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @program: pdd
 * @description: 库存消息处理
 * @create: 2020-12-11 20:40
 **/
@Component
public class SkuMessageReceiver {


    @Resource
    private PddGoodsService pddGoodsService;

    @RabbitListener(queues = RabbitMQConfig.SKU_DECR)
    public void skuDecrProcess(Map<String, Object> msg, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try{
            OrderVo orderVo=(OrderVo)msg.get("orderVo");
            pddGoodsService.updateProductSkuStockByList(orderVo.getProducts());
            ch.basicAck(tag, false);// 消费确认
        }catch (Exception e){
            ch.basicNack(tag, false, true);
        }
    }
}
