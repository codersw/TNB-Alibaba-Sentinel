package com.alibaba.csp.sentinel.dashboard.constant;

/**
 * 规则前缀
 * @author shaowen
 */
public class RuleConsts {
    //限流规则key前缀
    public final static String RULE_FLOW = "sentinel_rule_flow_";
    public final static String RULE_FLOW_CHANNEL = "sentinel_rule_flow_channel";
    //降级规则key前缀
    public final static String RULE_DEGRADE = "sentinel_rule_degrade_";
    public final static String RULE_DEGRADE_CHANNEL = "sentinel_rule_degrade_channel";
    //系统规则key前缀
    public final static String RULE_SYSTEM = "sentinel_rule_system_";
    public final static String RULE_SYSTEM_CHANNEL = "sentinel_rule_system_channel";
    //授权规则key前缀
    public final static String RULE_AUTH = "sentinel_rule_auth_";
    public final static String RULE_AUTH_CHANNEL = "sentinel_rule_auth_channel";
    //热点规则key前缀
    public final static String RULE_PARAM_FlOW = "sentinel_rule_param_flow_";
    public final static String RULE_PARAM_FlOW_CHANNEL = "sentinel_rule_param_flow_channel";
}
