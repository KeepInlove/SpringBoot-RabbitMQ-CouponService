package com.example.springbootmybatisplus.producer;

import com.example.springbootmybatisplus.config.RabbitMQConfig;
import com.example.springbootmybatisplus.req.DistributionReq;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-16 15:24
 * @email gxy06x@qq.com
 */
@Service
@Slf4j
public class CouponMessageProducer extends MessageProducer {

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();

    //优惠券消息生产者
    public void produceMessage(DistributionReq req) {
        String message = GSON.toJson(req);
        super.produceMessage(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
        log.info("消息发送成功, requestId={}", req.getRequestId());
    }
}
