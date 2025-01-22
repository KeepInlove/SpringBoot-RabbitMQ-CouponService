package com.example.springbootmybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springbootmybatisplus.domain.CouponCode;

public interface CouponCodeService extends IService<CouponCode> {

    boolean redeemCoupon(Long userId, String code);
}
