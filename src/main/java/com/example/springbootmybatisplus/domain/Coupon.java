package com.example.springbootmybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;
/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-15 16:09
 * @email gxy06x@qq.com
 */
@TableName("coupon")
@Data
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private BigDecimal discount;
    private Date startTime;
    private Date endTime;
    private Timestamp createdAt;
}
