package com.example.springbootmybatisplus.controller;

import com.example.springbootmybatisplus.domain.Coupon;
import com.example.springbootmybatisplus.domain.CouponStock;
import com.example.springbootmybatisplus.producer.CouponMessageProducer;
import com.example.springbootmybatisplus.req.CouponReq;
import com.example.springbootmybatisplus.req.DistributionReq;
import com.example.springbootmybatisplus.service.CouponService;
import com.example.springbootmybatisplus.service.CouponStockService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Guoxinyu
 * @description: 优惠券/密钥 控制层
 * @date 2025-01-16 13:19
 * @email gxy06x@qq.com
 */
@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();

    @Autowired
    private CouponService couponService;

    @Resource
    private CouponStockService couponStockService;
    @Autowired
    private  CouponMessageProducer couponMessageProducer;
    @PostMapping("/create")
    public Coupon createCoupon(@RequestBody CouponReq req) {
        return couponService.createCoupon(req, req.getStockCount());
    }

    @PostMapping("/getCoupon")
    public String getCoupon(@RequestBody DistributionReq req) {
        boolean coupon = couponService.getCoupon(req);
        if (coupon) {
            return "优惠券领取成功";
        } else {
            return "优惠券领取失败";
        }
    }

    @PostMapping("/asyncCoupon")
    public Map<String, Object> asyncCoupon(@RequestBody DistributionReq req) {
        // 生成唯一请求ID，防止重复
        String requestId = UUID.randomUUID().toString();
        req.setRequestId(requestId);
        // 将请求入队
        couponMessageProducer.produceMessage(req);
        Map<String, Object> resp= new HashMap<>();
        resp.put("requestId", requestId);
        resp.put("text", "优惠券领取成功");
        resp.put("status_code",200);
        return resp;
    }

    //查询
    @GetMapping("/query/{couponId}")
    public Map<String, Object> queryCoupon(@PathVariable Long couponId) {
        Coupon coupon = couponService.getById(couponId);
        CouponStock couponStock = couponStockService.getById(couponId);
        Map<String, Object> map = new HashMap<>();
        map.put("id", coupon.getId());
        map.put("name", coupon.getName());
        map.put("description", coupon.getDescription());
        map.put("discount", coupon.getDiscount());
        map.put("startTime", coupon.getStartTime());
        map.put("endTime", coupon.getEndTime());
        map.put("totalStock",couponStock.getTotalStock());
        map.put("remainingStock",couponStock.getRemainingStock());
        return map;
    }


}
