/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.redis;


import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;


/**
 * redis拉取规则
 * @author shaowen
 */
// @Component
public class RedisRuleProvider{

    @Autowired
    private RedisClient client;


    public <T> List<T> getRules(String appName, String ruleType, Class<?> c) {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubCommands<String, String> subCommands = connection.sync();
        String rules = subCommands.get(ruleType + appName);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<T>();
        }
        return (List<T>) JSONObject.parseArray(rules,c);
    }
}
