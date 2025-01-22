package com.example.springbootmybatisplus.config;
import org.redisson.config.Config;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: TODO 
 * @author Guoxinyu
 * @date 2025-01-17 16:34
 * @email gxy06x@qq.com
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379"); // 根据实际情况修改地址和端口
        return Redisson.create(config);
    }
}