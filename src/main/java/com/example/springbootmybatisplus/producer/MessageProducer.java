package com.example.springbootmybatisplus.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-17 14:01
 * @email gxy06x@qq.com
 */
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //生产消息
    public void produceMessage(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
