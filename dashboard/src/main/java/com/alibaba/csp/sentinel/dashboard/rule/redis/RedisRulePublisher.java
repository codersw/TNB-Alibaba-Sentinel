package com.alibaba.csp.sentinel.dashboard.rule.redis;

import com.alibaba.csp.sentinel.dashboard.constant.RuleConsts;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * redis推送规则
 * @author
 */
// @Component
public class RedisRulePublisher {

    @Autowired
    private RedisClient client;

    public void publish(String app, List rules, String ruleType, String ruleTypeChannel){
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubCommands<String, String> subCommands = connection.sync();
        String value = JSON.toJSONString(rules);
        subCommands.multi();
        subCommands.set(ruleType + app,value);
        subCommands.publish(ruleTypeChannel, value);
        subCommands.exec();
    }
}
