package com.alibaba.csp.sentinel.dashboard;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 注入redis客户端不知道为什么用Lettuce
 * 为了兼容Sentinel只好用这个
 * @author shaowen
 */
@Component
public class RedisClientInit {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Bean
    public RedisClient redisClient() {
        RedisURI.Builder redisUriBuilder = RedisURI.builder();
        redisUriBuilder.withHost(host).withPort(port);
        return RedisClient.create(redisUriBuilder.build());
    }
}
