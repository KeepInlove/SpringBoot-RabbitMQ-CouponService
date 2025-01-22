package com.example.springbootmybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootmybatisplus.domain.Coupon;
import com.example.springbootmybatisplus.domain.CouponCode;
import com.example.springbootmybatisplus.domain.CouponStock;
import com.example.springbootmybatisplus.mapper.CouponCodeMapper;
import com.example.springbootmybatisplus.mapper.CouponMapper;
import com.example.springbootmybatisplus.mapper.CouponStockMapper;
import com.example.springbootmybatisplus.producer.CouponMessageProducer;
import com.example.springbootmybatisplus.req.CouponReq;
import com.example.springbootmybatisplus.req.DistributionReq;
import com.example.springbootmybatisplus.service.CouponService;
import com.example.springbootmybatisplus.util.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-16 10:37
 * @email gxy06x@qq.com
 */
@Service
@Slf4j
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private CouponStockMapper couponStockMapper;

    @Autowired
    private CouponCodeMapper couponCodeMapper;
    @Autowired
    private CouponMessageProducer couponMessageProducer;
    @Autowired
    private RedisRedissonService redisRedissonService;

    /***
     * @description: 试试发放券扣减库存
     * @param: req
     * @author Guoxinyu
     */
    @Transactional
    @Override
    public boolean getCoupon(DistributionReq req) {
        Long couponId = req.getCouponId();
        Long userId = req.getUserId();
        int num = req.getNum();
        String lockKey = "couponLock:" + couponId; // 为每个优惠券ID创建一个锁
        RLock lock = redisRedissonService.getWriteLock(lockKey);
        try {
            // 尝试预扣减库存
            //使用 SETNX 尝试设置一个键，如果键不存在则设置成功并返回 true，表示获取锁成功。
            //同时设置键的过期时间（TTL），以防止死锁。
            //释放锁：
            //使用 Lua 脚本确保只有持有锁的线程可以释放锁，避免误删其他线程持有的锁。
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("获取锁失败，无法处理优惠券请求: userId={}, couponId={}, num={}", userId, couponId, num);
                return false;
            }
            CouponStock couponStock = couponStockMapper.selectById(couponId);
            if (couponStock != null && couponStock.getRemainingStock() >= num) {
                int updatedRows = couponStockMapper.reduceStockWithOptimisticLock(couponId, num, couponStock.getVersion());
                if (updatedRows > 0) {
                    log.info("生产消息发放优惠券请求已发送: userId={}, couponId={}, num={}", userId, couponId, num);
                    return true;
                } else {
                    // 库存扣减失败，可能由于并发冲突
                    log.error("库存更新失败，可能是并发导致的版本冲突");
                    return false;
                }
            } else {
                // 库存不足
                log.error("库存不足，无法发放优惠券");
                return false;
            }
        }catch (InterruptedException e){
            log.error("线程中断: {}", e.getMessage());
            Thread.currentThread().interrupt(); // 重新设置中断状态
            return false;
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }


    @Transactional
    @Override
    public Coupon createCoupon(CouponReq req, int stockCount) {
        Coupon coupon = new Coupon();
        coupon.setName(req.getName());
        coupon.setDescription(req.getDescription());
        coupon.setDiscount(req.getDiscount());
        coupon.setStartTime(req.getStartTime());
        coupon.setEndTime(req.getEndTime());
        // 保存优惠券
        couponMapper.insert(coupon);
        // 生成兑换码
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < stockCount; i++) {
            codes.add(CodeGenerator.generateCode());
        }
        // 保存库存信息
        CouponStock couponStock = new CouponStock();
        couponStock.setCouponId(coupon.getId());
        couponStock.setTotalStock(stockCount);
        couponStock.setRemainingStock(stockCount);
        couponStockMapper.insert(couponStock);
        List<CouponCode> couponCodes = new ArrayList<>();
        // 保存兑换码
        for (String code : codes) {
            CouponCode couponCode = new CouponCode();
            couponCode.setCouponId(coupon.getId());
            couponCode.setCode(code);
            couponCodes.add(couponCode);
        }
        couponCodeMapper.insert(couponCodes);
        return coupon;
    }



}
