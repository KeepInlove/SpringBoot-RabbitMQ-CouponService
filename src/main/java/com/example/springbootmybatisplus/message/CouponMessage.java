package com.example.springbootmybatisplus.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-16 15:24
 * @email gxy06x@qq.com
 */
@Data
public class CouponMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private Long couponId;
    private int num;
}
