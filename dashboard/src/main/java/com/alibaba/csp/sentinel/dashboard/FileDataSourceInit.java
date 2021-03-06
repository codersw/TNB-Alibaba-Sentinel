package com.alibaba.csp.sentinel.dashboard;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.command.handler.ModifyParamFlowRulesCommandHandler;
import com.alibaba.csp.sentinel.dashboard.config.DashboardConfig;
import com.alibaba.csp.sentinel.datasource.*;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mango.common.FileConsts;
import java.util.List;
import java.util.Set;

/**
 * 文件数据源注入
 * @author shaowen
 */
public class FileDataSourceInit implements InitFunc {

    @Override
    public void init() throws Exception {

        String ruleDir = DashboardConfig.getConfigStr("user.home") + FileConsts.DIR;
        String flowRulePath = ruleDir + FileConsts.FLOW_RULE;
        String gatewayFlowRulePath = ruleDir + FileConsts.GATEWAY_FLOW_RULE;
        String gatewayApiDefinitionPath = ruleDir + FileConsts.GATEWAY_API_DEFINITION;
        String degradeRulePath = ruleDir + FileConsts.DEGRADE_RULE;
        String systemRulePath = ruleDir + FileConsts.SYSTEM_RULE;
        String authorityRulePath = ruleDir + FileConsts.AUTHORITY_RULE;
        String hotParamFlowRulePath = ruleDir + FileConsts.PARAM_FLOW_RULE;

        // 网关API分组
        ReadableDataSource<String, Set<ApiDefinition>> gatewayApiDefinitionDS = new FileRefreshableDataSource<>(
                gatewayApiDefinitionPath,
                source -> JSON.parseObject(source, new TypeReference<Set<ApiDefinition>>() {})
        );
        GatewayApiDefinitionManager.register2Property(gatewayApiDefinitionDS.getProperty());
        // TODO 这里写这些视乎没有什么用 sentinel 没有封装写入文件网关API的方法or我没有找到 但我已经手动实现了

        // 网关流控规则
        ReadableDataSource<String, Set<GatewayFlowRule>> gatewayFlowRuleDS = new FileRefreshableDataSource<>(
                gatewayFlowRulePath,
                source -> JSON.parseObject(source, new TypeReference<Set<GatewayFlowRule>>() {})
        );
        GatewayRuleManager.register2Property(gatewayFlowRuleDS.getProperty());
        // TODO 这里写这些视乎没有什么用 sentinel 没有封装写入文件网关配置的方法or我没有找到 但我已经手动实现了

        // 流控规则
        ReadableDataSource<String, List<FlowRule>> flowRuleRDS = new FileRefreshableDataSource<>(
                flowRulePath,
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {})
        );
        // 将可读数据源注册至FlowRuleManager
        // 这样当规则文件发生变化时，就会更新规则到内存
        FlowRuleManager.register2Property(flowRuleRDS.getProperty());
        WritableDataSource<List<FlowRule>> flowRuleWDS = new FileWritableDataSource<>(
                flowRulePath,
                this::encodeJson
        );
        // 将可写数据源注册至transport模块的WritableDataSourceRegistry中
        // 这样收到控制台推送的规则时，Sentinel会先更新到内存，然后将规则写入到文件中
        WritableDataSourceRegistry.registerFlowDataSource(flowRuleWDS);

        // 降级规则
        ReadableDataSource<String, List<DegradeRule>> degradeRuleRDS = new FileRefreshableDataSource<>(
                degradeRulePath,
                source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {})
        );
        DegradeRuleManager.register2Property(degradeRuleRDS.getProperty());
        WritableDataSource<List<DegradeRule>> degradeRuleWDS = new FileWritableDataSource<>(
                degradeRulePath,
                this::encodeJson
        );
        WritableDataSourceRegistry.registerDegradeDataSource(degradeRuleWDS);

        // 系统规则
        ReadableDataSource<String, List<SystemRule>> systemRuleRDS = new FileRefreshableDataSource<>(
                systemRulePath,
                source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {})
        );
        SystemRuleManager.register2Property(systemRuleRDS.getProperty());
        WritableDataSource<List<SystemRule>> systemRuleWDS = new FileWritableDataSource<>(
                systemRulePath,
                this::encodeJson
        );
        WritableDataSourceRegistry.registerSystemDataSource(systemRuleWDS);

        // 授权规则
        ReadableDataSource<String, List<AuthorityRule>> authorityRuleRDS = new FileRefreshableDataSource<>(
                flowRulePath,
                source -> JSON.parseObject(source, new TypeReference<List<AuthorityRule>>() {})
        );
        AuthorityRuleManager.register2Property(authorityRuleRDS.getProperty());
        WritableDataSource<List<AuthorityRule>> authorityRuleWDS = new FileWritableDataSource<>(
                authorityRulePath,
                this::encodeJson
        );
        WritableDataSourceRegistry.registerAuthorityDataSource(authorityRuleWDS);

        // 热点参数规则
        ReadableDataSource<String, List<ParamFlowRule>> hotParamFlowRuleRDS = new FileRefreshableDataSource<>(
                hotParamFlowRulePath,
                source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {})
        );
        ParamFlowRuleManager.register2Property(hotParamFlowRuleRDS.getProperty());
        WritableDataSource<List<ParamFlowRule>> paramFlowRuleWDS = new FileWritableDataSource<>(
                hotParamFlowRulePath,
                this::encodeJson
        );
        ModifyParamFlowRulesCommandHandler.setWritableDataSource(paramFlowRuleWDS);
    }

    /**
     * 转换字符串
     * @param t
     * @param <T>
     * @return
     */
    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }
}
