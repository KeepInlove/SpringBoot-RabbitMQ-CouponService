package com.example.springbootmybatisplus.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-16 11:34
 * @email gxy06x@qq.com
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "coupon-exchange";
    public static final String QUEUE_NAME = "coupon-queue";
    public static final String ROUTING_KEY = "coupon.routing.key";

    // 定义队列
    @Bean
    public Queue couponQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", "dlx-exchange")  // 绑定死信交换机
                .withArgument("x-dead-letter-routing-key", "dlx.routing.key")  // 绑定死信路由键
                .build();
    }

    // 定义交换机
    @Bean
    public TopicExchange couponExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    // 绑定队列到交换机，使用 routing key
    @Bean
    public Binding bindingCouponQueue() {
        return BindingBuilder.bind(couponQueue()).to(couponExchange()).with(ROUTING_KEY);
    }
}