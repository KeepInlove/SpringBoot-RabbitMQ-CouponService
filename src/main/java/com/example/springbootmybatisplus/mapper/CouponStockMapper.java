package com.example.springbootmybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootmybatisplus.domain.CouponStock;
import org.apache.ibatis.annotations.Param;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-15 16:11
 * @email gxy06x@qq.com
 */
public interface CouponStockMapper extends BaseMapper<CouponStock> {

    int reduceStockWithOptimisticLock(@Param("couponId") Long couponId, @Param("num")int num,@Param("version") int version);

    // 新增加库存的方法
    int increaseStockWithOptimisticLock(@Param("couponId") Long couponId, @Param("num") int num,@Param("version")int version);
}