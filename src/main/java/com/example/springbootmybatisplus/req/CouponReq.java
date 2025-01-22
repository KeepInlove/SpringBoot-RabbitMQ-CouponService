package com.example.springbootmybatisplus.req;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-16 17:18
 * @email gxy06x@qq.com
 */
@Data
public class CouponReq {
    private String name;
    private String description;
    private BigDecimal discount;
    private Date startTime;
    private Date endTime;
    private int stockCount;
}
