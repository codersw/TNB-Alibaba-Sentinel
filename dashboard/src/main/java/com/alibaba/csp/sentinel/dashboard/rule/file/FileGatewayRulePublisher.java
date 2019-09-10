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

import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.config.DashboardConfig;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.List;


/**
 * sentinel没有封装网关规则写入文件方法
 * 所以自己封装写入文件
 * @author shaowen
 */
@Component("fileGatewayRulePublisher")
public class FileGatewayRulePublisher {

    @Autowired
    private SentinelApiClient sentinelApiClient;

    public void publish(String app, String ip, int port, List<GatewayFlowRuleEntity> rules) throws Exception {
        if (StringUtil.isBlank(app)) {
            return;
        }
        if (rules == null) {
            return;
        }
        sentinelApiClient.modifyGatewayFlowRules(app, ip, port, rules);
        saveDataToFile(JSON.toJSONString(rules));
    }

    private void saveDataToFile(String data) throws Exception{
        String ruleDir = DashboardConfig.getConfigStr("user.home") + "/sentinel/rules";
        String gatewayFlowRulePath = ruleDir + "/gateway-flow-rule.json";
        BufferedWriter writer = null;

        File file = new File(gatewayFlowRulePath);
        //如果文件不存在，则新建一个
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception(e.getMessage());
            }
        }
        //写入
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception(e.getMessage());
            }
        }
        System.out.println("文件写入成功！");
    }
}
