package com.example.springbootmybatisplus.exception;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-17 14:57
 * @email gxy06x@qq.com
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}