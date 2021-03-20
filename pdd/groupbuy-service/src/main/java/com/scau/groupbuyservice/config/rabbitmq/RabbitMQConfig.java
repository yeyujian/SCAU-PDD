package com.scau.groupbuyservice.config.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: pdd
 * @description: rabbitmq配置文件
 * @create: 2020-12-10 20:09
 **/
@Configuration
public class RabbitMQConfig {

    private Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);
    // 绑定键
    public final static String  ORDER_INSERT= "order.insert"; // 好友请求发送

    public final static String  SKU_DECR="sku.decr"; // 减少库存请求发送

    @Bean
    public Queue OrderInsertQueue(){
        return new Queue(RabbitMQConfig.ORDER_INSERT);
    }

    @Bean
    public Queue SkuDecrQueue(){ return new Queue(RabbitMQConfig.SKU_DECR); }

    @Bean
    TopicExchange orderExchange() {
        return new TopicExchange("OrderExchange");
    }

    @Bean
    TopicExchange skuExchange() {
        return new TopicExchange("SkuExchange");
    }

    @Bean
    Binding bindingExchangeMessage_OrderInsert() {
        return BindingBuilder.bind(OrderInsertQueue()).to(orderExchange()).with(ORDER_INSERT);
    }

    @Bean
    Binding bindingExchangeMessage_SkuDecr() {
        return BindingBuilder.bind(SkuDecrQueue()).to(skuExchange()).with(SKU_DECR);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true
        rabbitTemplate.setMandatory(true);
        // 如果消息没有到exchange,则confirm回调,ack=false; 如果消息到达exchange,则confirm回调,ack=true
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(ack){
                    logger.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
                }else{
                    logger.info("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
                }
            }
        });

        //如果exchange到queue成功,则不回调return;如果exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                logger.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });
        return rabbitTemplate;
    }
}
