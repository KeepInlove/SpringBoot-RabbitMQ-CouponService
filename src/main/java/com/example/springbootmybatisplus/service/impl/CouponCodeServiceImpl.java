package com.example.springbootmybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootmybatisplus.domain.CouponCode;
import com.example.springbootmybatisplus.domain.UserCoupon;
import com.example.springbootmybatisplus.mapper.CouponCodeMapper;
import com.example.springbootmybatisplus.mapper.UserCouponMapper;
import com.example.springbootmybatisplus.service.CouponCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-17 9:57
 * @email gxy06x@qq.com
 */
@Service
@Slf4j
public class CouponCodeServiceImpl extends ServiceImpl<CouponCodeMapper, CouponCode> implements CouponCodeService {


    @Autowired
    private CouponCodeMapper couponCodeMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    /***
     * @description:  兑换优惠券
     * @param: userId,code
     * @author Guoxinyu
     */
    @Transactional
    public boolean redeemCoupon(Long userId, String code) {
        CouponCode couponCode = couponCodeMapper.selectOne(new LambdaQueryWrapper<CouponCode>().eq(CouponCode::getCode, code));
        if (couponCode != null && !couponCode.isUsed()) {
            log.error("兑换码无效或已使用: code={}", code);
            return false;
        }
        UserCoupon userCoupon = userCouponMapper.selectOne(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponCodeId, couponCode.getId()));
        if (userCoupon != null) {
            // 标记兑换码为已使用
            couponCode.setUsed(true);
            couponCode.setUserId(userId);
            couponCode.setUsedAt(new Date());
            couponCodeMapper.updateById(couponCode);
            log.info("用户已成功兑换优惠券: userId={}, couponCodeId={}", userId, couponCode.getId());
            return true;
        } else {
            log.error("用户未领取该优惠券: userId={}, couponCodeId={}", userId, couponCode.getId());
            return false;
        }
    }
}
