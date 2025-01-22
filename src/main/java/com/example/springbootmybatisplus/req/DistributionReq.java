package com.example.springbootmybatisplus.req;

import lombok.Data;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-17 14:12
 * @email gxy06x@qq.com
 */
@Data
public class DistributionReq {
    private Long userId;
    private Long couponId;
    private Integer num;
    private String requestId;
}
