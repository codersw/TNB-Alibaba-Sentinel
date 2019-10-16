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
import com.alibaba.csp.sentinel.dashboard.config.DashboardConfig;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.mango.common.FileConsts;
import com.mango.common.FileUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * sentinel没有封装网关api读取文件方法
 * 所以自己封装读取文件
 * @author shaowen
 */
@Component
public class FileGatewayApiProvider {

    public List<ApiDefinitionEntity> getRules(String app, String ip, Integer port) throws Exception {
        if (StringUtil.isBlank(app)) {
            return new ArrayList<>();
        }
        String value = FileUtils.getDatafromFile(SentinelConfig.getConfig("user.home")+ FileConsts.DIR, FileConsts.GATEWAY_API_DEFINITION);
        if (StringUtil.isBlank(value.replace("[]",""))) {
            return new ArrayList<>();
        } else {
            return JSON.parseArray(value,ApiDefinitionEntity.class).stream().filter(rule -> rule.getApp().equals(app) && rule.getIp().equals(ip) && rule.getPort().equals(port)).collect(Collectors.toList());
        }
    }
}
