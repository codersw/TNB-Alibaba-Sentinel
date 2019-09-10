package com.mango.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
        String ruleDir = SentinelConfig.getConfig("user.home") + "/sentinel/rules";
        String gatewayFlowRulePath = ruleDir + "/gateway-flow-rule.json";
        String gatewayApiDefinitionPath = ruleDir + "/gateway-api-definition.json";
        // 网关API分组
        ReadableDataSource<String, Set<ApiDefinition>> gatewayApiDefinitionDS = new FileRefreshableDataSource<>(
                gatewayApiDefinitionPath,
                apiDefinitionListParser
        );
        GatewayApiDefinitionManager.register2Property(gatewayApiDefinitionDS.getProperty());
        // 网关流控规则
        ReadableDataSource<String, Set<GatewayFlowRule>> gatewayFlowRuleDS = new FileRefreshableDataSource<>(
                gatewayFlowRulePath,
                gatewayFlowRuleListParser
        );
        GatewayRuleManager.register2Property(gatewayFlowRuleDS.getProperty());
        GatewayCallbackManager.setBlockHandler(new GatewayBlockRequestHandler());
    }
    /**
     * 网关API分组对象转换
     */
    private Converter<String, Set<ApiDefinition>> apiDefinitionListParser = source -> JSON.parseObject(
            source,
            new TypeReference<Set<ApiDefinition>>() {
            }
    );
    /**
     * 网关流控规则对象转换
     */
    private Converter<String, Set<GatewayFlowRule>> gatewayFlowRuleListParser = source -> JSON.parseObject(
            source,
            new TypeReference<Set<GatewayFlowRule>>() {
            }
    );
}
