package com.mango.gateway.service;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.fastjson.JSON;
import com.mango.common.FileConsts;
import com.mango.common.FileUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class RouteService implements ApplicationEventPublisherAware {

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;


    public void notifyChanged() throws Exception {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @PostConstruct
    private void init() throws Exception {
        this.list().forEach(routeDefinition -> routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe());
        notifyChanged();
    }

    public List<RouteDefinition> list() throws Exception {
        String value = FileUtils.getDatafromFile(SentinelConfig.getConfig("user.home") + FileConsts.DIR, FileConsts.GATEWAY_ROUTES);
        if(!value.equals("")){
            return JSON.parseArray(value,RouteDefinition.class);
        }else {
            return new ArrayList<>();
        }
    }

    /**
     * 增加路由
     * @param definition
     * @return
     */
    public void add(RouteDefinition definition) throws Exception{
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        notifyChanged();
    }

    /**
     * 更新路由
     * @param definition
     */
    public void update(RouteDefinition definition) throws Exception {
        this.routeDefinitionWriter.delete(Mono.just(definition.getId())).subscribe();
        this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        notifyChanged();
    }

    /**
     * 删除路由
     * @param id
     * @return
     */
    public void delete(String id) throws Exception{
        this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        notifyChanged();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
