package com.mango.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 * 启动时需要加如下vm参数
 * -Dcsp.sentinel.dashboard.server=localhost:8080
 * -Dcsp.sentinel.api.port=8720
 * -Dproject.name=gateway
 * -Dcsp.sentinel.app.type=1
 * @author shaowen
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
