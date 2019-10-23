package com.mango.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.mango.gateway.model.GatewayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * 自定义请求处理
 * @author shaowen
 */
@Slf4j
public class GatewayBlockRequestHandler implements BlockRequestHandler  {

    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        log.error("请求发生异常，请求URI：{}，请求方法：{}，异常信息：{}",
                exchange.getRequest().getPath(), exchange.getRequest().getMethodValue(), t.getMessage());
        int status = HttpStatus.CONTINUE.value();
        GatewayResponse errorResponse = new GatewayResponse();
        // 不同的异常返回不同的提示语
        if (t instanceof FlowException) {
            errorResponse = GatewayResponse.builder().code(-1).msg("接口限流了").build();
            status = HttpStatus.TOO_MANY_REQUESTS.value();
        } else if (t instanceof DegradeException) {
            errorResponse = GatewayResponse.builder().code(-1).msg("服务降级了").build();
            status = HttpStatus.TOO_MANY_REQUESTS.value();
        } else if (t instanceof ParamFlowException) {
            errorResponse = GatewayResponse.builder().code(-1).msg("接口限流了").build();
            status = HttpStatus.TOO_MANY_REQUESTS.value();
        } else if (t instanceof SystemBlockException) {
            errorResponse = GatewayResponse.builder().code(-1).msg("触发系统保护规则").build();
            status = HttpStatus.BAD_REQUEST.value();
        } else if (t instanceof AuthorityException) {
            errorResponse = GatewayResponse.builder().code(-1).msg("授权规则不通过").build();
            status = HttpStatus.UNAUTHORIZED.value();
        }
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(fromObject(errorResponse));
    }
}
