package com.example.springbootmybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-16 17:09
 * @email gxy06x@qq.com
 */

@TableName("coupon_code")
@Data
public class CouponCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long couponId;
    private Long userId;
    private String code;
    private boolean used;
    private int couponStatus;
    private Date createdAt;
    private Date usedAt;

}