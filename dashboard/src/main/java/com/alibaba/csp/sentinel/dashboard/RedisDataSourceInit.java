package com.alibaba.csp.sentinel.dashboard;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.dashboard.config.DashboardConfig;
import com.alibaba.csp.sentinel.dashboard.constant.RuleConsts;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.redis.RedisDataSource;
import com.alibaba.csp.sentinel.datasource.redis.config.RedisConnectionConfig;
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.stereotype.Component;
import java.util.List;


/**
 * 初始化时读取redis数据源数据
 * @author shaowen
 */
public class RedisDataSourceInit implements InitFunc {

    @Override
    public void init() throws Exception {
        RedisConnectionConfig config = RedisConnectionConfig.builder()
                .withHost(DashboardConfig.getConfigStr("redis.host"))
                .withPort(DashboardConfig.getConfigInt("redis.port",0,0))
                .build();
        // 流控
        Converter<String, List<FlowRule>> parser0 = source -> JSON.parseObject(source,new TypeReference<List<FlowRule>>() {});
        ReadableDataSource<String, List<FlowRule>> redisDataSource0 = new RedisDataSource<>(config, RuleConsts.RULE_FLOW + SentinelConfig.getAppName(), RuleConsts.RULE_FLOW_CHANNEL, parser0);
        FlowRuleManager.register2Property(redisDataSource0.getProperty());
        // 降级
        Converter<String, List<DegradeRule>> parser1 = source -> JSON.parseObject(source,new TypeReference<List<DegradeRule>>() {});
        ReadableDataSource<String, List<DegradeRule>> redisDataSource1 = new RedisDataSource<>(config, RuleConsts.RULE_DEGRADE + SentinelConfig.getAppName(), RuleConsts.RULE_DEGRADE_CHANNEL, parser1);
        DegradeRuleManager.register2Property(redisDataSource1.getProperty());
        // 系统
        Converter<String, List<SystemRule>> parser2 = source -> JSON.parseObject(source,new TypeReference<List<SystemRule>>() {});
        ReadableDataSource<String, List<SystemRule>> redisDataSource2 = new RedisDataSource<>(config, RuleConsts.RULE_SYSTEM + SentinelConfig.getAppName(), RuleConsts.RULE_SYSTEM_CHANNEL, parser2);
        SystemRuleManager.register2Property(redisDataSource2.getProperty());
        // 授权
        Converter<String, List<AuthorityRule>> parser3 = source -> JSON.parseObject(source,new TypeReference<List<AuthorityRule>>() {});
        ReadableDataSource<String, List<AuthorityRule>> redisDataSource3= new RedisDataSource<>(config, RuleConsts.RULE_AUTH + SentinelConfig.getAppName(), RuleConsts.RULE_AUTH_CHANNEL, parser3);
        AuthorityRuleManager.register2Property(redisDataSource3.getProperty());
        // 热点
        Converter<String, List<ParamFlowRule>> parser4 = source -> JSON.parseObject(source,new TypeReference<List<ParamFlowRule>>() {});
        ReadableDataSource<String, List<ParamFlowRule>> redisDataSource4= new RedisDataSource<>(config, RuleConsts.RULE_PARAM_FlOW + SentinelConfig.getAppName(), RuleConsts.RULE_PARAM_FlOW_CHANNEL, parser4);
        ParamFlowRuleManager.register2Property(redisDataSource4.getProperty());
    }
}
