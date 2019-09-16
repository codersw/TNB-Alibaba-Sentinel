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
package com.alibaba.csp.sentinel.dashboard.rule.file;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.mango.common.FileConsts;
import com.mango.common.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * sentinel没有封装网关规则写入文件方法
 * 所以自己封装写入文件
 * @author shaowen
 */
@Component("fileGatewayDegradePublisher")
public class FileGatewayDegradePublisher {

    @Autowired
    private SentinelApiClient sentinelApiClient;

    public void publish(String app, String ip, int port, List<DegradeRuleEntity> rules) throws Exception {
        if (StringUtil.isBlank(app)) {
            return;
        }
        if (rules == null) {
            return;
        }
        sentinelApiClient.setDegradeRuleOfMachine(app, ip, port, rules);
        FileUtils.saveDataToFile(SentinelConfig.getConfig("user.home")+ FileConsts.DIR, FileConsts.GATEWAY_DEGRADE_RULE, JSON.toJSONString(rules));
    }

}
