package com.example.springbootmybatisplus.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootmybatisplus.config.RabbitMQConfig;
import com.example.springbootmybatisplus.domain.CouponCode;
import com.example.springbootmybatisplus.domain.CouponStock;
import com.example.springbootmybatisplus.domain.UserCoupon;
import com.example.springbootmybatisplus.mapper.CouponCodeMapper;
import com.example.springbootmybatisplus.mapper.CouponStockMapper;
import com.example.springbootmybatisplus.mapper.UserCouponMapper;
import com.example.springbootmybatisplus.req.DistributionReq;
import com.example.springbootmybatisplus.service.impl.RedisRedissonService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Guoxinyu
 * @description: 优惠券消费者
 * @date 2025-01-16 11:33
 * @email gxy06x@qq.com
 */
@Component
@Slf4j
public class CouponConsumer {
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();

    @Autowired
    private CouponStockMapper couponStockMapper;
    @Autowired
    private CouponCodeMapper couponCodeMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private RedisRedissonService redisRedissonService;



    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME, ackMode = "MANUAL")
    public void consumeMessage(Message message, Channel channel) {
        DistributionReq req = GSON.fromJson(new String(message.getBody()), DistributionReq.class);
        log.info("开始处理请求: requestId={}, userId={}", req.getRequestId(), req.getUserId());

        // 获取 Redisson 分布式锁
        String lockKey = "couponLock:" + req.getCouponId();
        RLock lock = redisRedissonService.getWriteLock(lockKey);

        boolean lockAcquired = false;

        try {
            // 尝试获取锁，防止并发操作库存
            lockAcquired = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!lockAcquired) {
                log.error("获取锁失败，无法处理请求: requestId={}", req.getRequestId());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                return;
            }

            try {
                // 使用 RedisRedissonService 进行幂等性检查
                String redisKey = "coupon:request:" + req.getRequestId();
                if (redisRedissonService.exists(redisKey)) {
                    log.warn("请求已处理，跳过 requestId={}", req.getRequestId());
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }

                // 扣减库存
                CouponStock couponStock = couponStockMapper.selectById(req.getCouponId());
                if (couponStock != null && couponStock.getRemainingStock() >= req.getNum()) {
                    int updatedRows = couponStockMapper.reduceStockWithOptimisticLock(
                            req.getCouponId(), req.getNum(), couponStock.getVersion());

                    if (updatedRows > 0) {
                        // 发放优惠券

                        distributeCoupons(req.getUserId(), req.getCouponId(), req.getNum());
                        // 标记请求已处理，防止重复消费，设置过期时间为1小时
                        redisRedissonService.setString(redisKey, "processed", 1, TimeUnit.HOURS);

                        log.info("优惠券成功发放给用户: requestId={}, userId={}", req.getRequestId(), req.getUserId());
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    } else {
                        log.warn("库存不足, requestId={}", req.getRequestId());
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    }
                } else {
                    log.warn("库存不足，无法发放: requestId={}", req.getRequestId());
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                }

            } catch (IOException ioException) {
                log.error("消息确认/拒绝时发生 I/O 异常, requestId={}, 错误={}", req.getRequestId(), ioException.getMessage());
                nackMessage(channel, message.getMessageProperties().getDeliveryTag());
            }

        } catch (InterruptedException e) {
            log.error("线程中断, requestId={}, 错误={}", req.getRequestId(), e.getMessage());
            nackMessage(channel, message.getMessageProperties().getDeliveryTag());
        } catch (Exception e) {
            log.error("处理请求失败, requestId={}, 错误={}", req.getRequestId(), e.getMessage());
            nackMessage(channel, message.getMessageProperties().getDeliveryTag());
        } finally {
            // 确保在任何情况下释放锁
            if (lockAcquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    private void nackMessage(Channel channel, long deliveryTag) {
        try {
            channel.basicNack(deliveryTag, false, true);
        } catch (IOException e) {
            log.error("消息重新入队失败: {}", e.getMessage());
        }
    }
    public void distributeCoupons(Long userId,Long couponId, int num) {
        List<CouponCode> couponCodes = couponCodeMapper.selectList(
                new LambdaQueryWrapper<CouponCode>()
                        .eq(CouponCode::getCouponId, couponId)
                        .eq(CouponCode::getCouponStatus, 0)  // 0 代表未发放
                        .last("limit " + num));
        for (CouponCode couponCode : couponCodes) {
            // 更新优惠券码状态为已发放
            couponCode.setCouponStatus(1); // 1 代表已发放
            couponCode.setUserId(userId);
            couponCodeMapper.updateById(couponCode);
            // 创建用户优惠券记录
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponCodeId(couponCode.getId());
            userCouponMapper.insert(userCoupon);
        }
    }

}