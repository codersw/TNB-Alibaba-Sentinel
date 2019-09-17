package com.mango.gateway.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;


/**
 * 打印请求参数及统计执行时长过滤器
 * @author shaowen
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Log logger = LogFactory.getLog(LoggingFilter.class);
    private static final String START_TIME = "startTime";

    public LoggingFilter() {
        logger.info("Loaded GlobalFilter [Logging]");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String info = String.format("Method:{%s} Host:{%s} Path:{%s} Query:{%s}",
                Objects.requireNonNull(exchange.getRequest().getMethod()).name(),
                exchange.getRequest().getURI().getHost(),
                exchange.getRequest().getURI().getPath(),
                exchange.getRequest().getQueryParams());
        logger.info(info);
        String hostler = Objects.requireNonNull(exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)).toString().replace("http://","").replace("https://","").split("/")[0];
        logger.info(hostler);
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then( Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                long executeTime = (System.currentTimeMillis() - startTime);
                logger.info(exchange.getRequest().getURI().getRawPath() + " : " + executeTime + "ms");
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
