package com.mango.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mango.common.FileConsts;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;
import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * sentinel gateway核心配置
 * @author shaowen
 */
@Configuration
public class GatewayConfiguration {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void doInit() throws FileNotFoundException {
        // 网关API分组
        ReadableDataSource<String, Set<ApiDefinition>> gatewayApiDefinitionDS = new FileRefreshableDataSource<>(
                SentinelConfig.getConfig("user.home") + FileConsts.DIR + FileConsts.GATEWAY_API_DEFINITION,
                source -> JSON.parseObject(source, new TypeReference<Set<ApiDefinition>>() {})
        );
        GatewayApiDefinitionManager.register2Property(gatewayApiDefinitionDS.getProperty());
        // 网关流控规则
        ReadableDataSource<String, Set<GatewayFlowRule>> gatewayFlowRuleDS = new FileRefreshableDataSource<>(
                SentinelConfig.getConfig("user.home") + FileConsts.DIR + FileConsts.GATEWAY_FLOW_RULE,
                source -> JSON.parseObject(source, new TypeReference<Set<GatewayFlowRule>>() {})
        );
        GatewayRuleManager.register2Property(gatewayFlowRuleDS.getProperty());
        // 降级规则
        ReadableDataSource<String, List<DegradeRule>> degradeRuleRDS = new FileRefreshableDataSource<>(
                SentinelConfig.getConfig("user.home") + FileConsts.DIR + FileConsts.GATEWAY_DEGRADE_RULE,
                source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {})
        );
        DegradeRuleManager.register2Property(degradeRuleRDS.getProperty());
        GatewayCallbackManager.setBlockHandler(new GatewayBlockRequestHandler());
    }


}
