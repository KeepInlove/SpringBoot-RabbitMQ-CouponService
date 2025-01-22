package com.example.springbootmybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-15 16:10
 * @email gxy06x@qq.com
 */
@Data
@TableName("user_coupon")
public class UserCoupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long couponCodeId;
    private Long userId;
    private Timestamp grabbedAt;
}
