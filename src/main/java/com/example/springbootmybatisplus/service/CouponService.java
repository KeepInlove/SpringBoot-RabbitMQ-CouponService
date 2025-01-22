package com.example.springbootmybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springbootmybatisplus.domain.Coupon;
import com.example.springbootmybatisplus.req.CouponReq;
import com.example.springbootmybatisplus.req.DistributionReq;

public interface CouponService extends IService<Coupon> {
    //发券接口
    boolean getCoupon(DistributionReq req);

    Coupon createCoupon(CouponReq req, int stockCount);

}
