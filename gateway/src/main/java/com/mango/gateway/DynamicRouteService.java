package com.mango.gateway;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.fastjson.JSON;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class DynamicRouteService implements ApplicationEventPublisherAware {

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    private void notifyChanged() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @PostConstruct
    public void init() throws IOException {
        String value = getDatafromFile();
        if(!value.equals("")){
            JSON.parseArray(value,RouteDefinition.class).forEach(routeDefinition -> routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe());
            notifyChanged();
        }
    }

    /**
     * 增加路由
     * @param definition
     * @return
     */
    public String add(RouteDefinition definition) {
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        notifyChanged();
        return "success";
    }

    /**
     * 更新路由
     * @param definition
     * @return
     */
    public String update(RouteDefinition definition) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception e) {
            return "update fail,not find route  routeId: " + definition.getId();
        }
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            notifyChanged();
            return "success";
        } catch (Exception e) {
            return "update route  fail";
        }
    }

    /**
     * 删除路由
     * @param id
     * @return
     */
    public String delete(String id) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(id));
            notifyChanged();
            return "delete success";
        } catch (Exception e) {
            e.printStackTrace();
            return "delete fail";
        }

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    private String getDatafromFile() throws IOException {
        String ruleDir = SentinelConfig.getConfig("user.home") + "/sentinel/rules";
        String gatewayFlowRulePath = ruleDir + "/geteway-routes.json";
        this.mkdirIfNotExits(ruleDir);
        this.createFileIfNotExits(gatewayFlowRulePath);
        BufferedReader reader = null;
        StringBuilder laststr = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(gatewayFlowRulePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                laststr.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("文件读取成功："+ laststr);
        return laststr.toString();
    }

    private void saveDataToFile(String data) throws Exception{
        String ruleDir = SentinelConfig.getConfig("user.home") + "/sentinel/rules";
        String gatewayFlowRulePath = ruleDir + "/geteway_routes.json";
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
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), StandardCharsets.UTF_8));
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

    /**
     * 创建目录
     *
     * @param filePath
     */
    private void mkdirIfNotExits(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 创建文件
     *
     * @param filePath
     * @throws IOException
     */
    private void createFileIfNotExits(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

}
