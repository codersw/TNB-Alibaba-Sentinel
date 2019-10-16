package com.alibaba.csp.sentinel.dashboard.service;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.RouteDefinitionEntity;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.mango.common.FileConsts;
import com.mango.common.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    private SentinelApiClient sentinelApiClient;

    /**
     * 获取列表
     * @param app
     * @param ip
     * @param port
     * @return
     * @throws Exception
     */
    public List<RouteDefinitionEntity> list(String app, String ip, Integer port) throws Exception {
        String value = FileUtils.getDatafromFile(SentinelConfig.getConfig("user.home") + FileConsts.DIR, FileConsts.GATEWAY_ROUTES);
        if (!StringUtil.isBlank(value.replace("[]",""))) {
            return JSON.parseArray(value, RouteDefinitionEntity.class).stream().filter(rule -> rule.getApp().equals(app) && rule.getIp().equals(ip) && rule.getPort().equals(port)).collect(Collectors.toList());
        }else {
            return new ArrayList<>();
        }
    }

    /**
     * 增加路由
     * @param definition
     * @return
     */
    public void add(RouteDefinitionEntity definition) throws Exception {
        List<RouteDefinitionEntity> array = list(definition.getApp(),definition.getIp(),definition.getPort());
        if(array.stream().anyMatch(e -> definition.getId().equals(e.getId()))){
                throw new Exception(definition.getId() + "已存在");
        };
        sentinelApiClient.setRoutes(definition.getApp(),definition.getIp(),definition.getPort(),definition);
        array.add(definition);
        FileUtils.saveDataToFile(SentinelConfig.getConfig("user.home") + FileConsts.DIR, FileConsts.GATEWAY_ROUTES, JSON.toJSONString(array));
    }

    /**
     * 更新路由
     * @param definition
     */
    public void update(RouteDefinitionEntity definition) throws Exception {
        sentinelApiClient.modifyRoutes(definition.getApp(),definition.getIp(),definition.getPort(),definition);
        List<RouteDefinitionEntity> array = list(definition.getApp(),definition.getIp(),definition.getPort());
        array.removeIf( r -> r.getId().equals(definition.getId()));
        array.add(definition);
        FileUtils.saveDataToFile(SentinelConfig.getConfig("user.home") + FileConsts.DIR, FileConsts.GATEWAY_ROUTES, JSON.toJSONString(array));
    }

    /**
     * 删除路由
     * @param app
     * @param ip
     * @param port
     * @param id
     * @throws Exception
     */
    public void delete(String app, String ip, Integer port,String id) throws Exception{
        sentinelApiClient.deleteRoutes(app,ip,port,id);
        List<RouteDefinitionEntity> array = list(app,ip,port);
        array.removeIf( r -> r.getId().equals(id));
        FileUtils.saveDataToFile(SentinelConfig.getConfig("user.home") + FileConsts.DIR, FileConsts.GATEWAY_ROUTES, JSON.toJSONString(array));
    }

}
