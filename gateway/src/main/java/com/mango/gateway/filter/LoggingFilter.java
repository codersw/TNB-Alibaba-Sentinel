package com.mango.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;


/**
 * 打印请求参数及统计执行时长过滤器
 * @author shaowen
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String START_TIME = "startTime";

    public LoggingFilter() {
        log.info("Loaded GlobalFilter [Logging]");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        ServerHttpRequest request = exchange.getRequest();
        assert url != null;
        assert route != null;
        log.info("路由ID:{},请求方法:{},请求路径:{},目标地址:{}://{}{},转发时间:{}",
                route.getId(), Objects.requireNonNull(request.getMethod()).name(), url.getPath(), url.getScheme(), url.getAuthority(), url.getPath(), LocalDateTime.now()
        );
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then( Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            assert startTime != null;
            long executeTime = (System.currentTimeMillis() - startTime);
            log.info("耗时:{}ms", executeTime);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
