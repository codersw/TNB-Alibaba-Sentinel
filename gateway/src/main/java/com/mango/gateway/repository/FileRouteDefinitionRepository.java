package com.mango.gateway.repository;


import com.alibaba.fastjson.JSON;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.*;


// @Component
public class FileRouteDefinitionRepository implements RouteDefinitionRepository {

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        RouteDefinition definition = new RouteDefinition();
        definition.setId("id");
        URI uri = UriComponentsBuilder.fromHttpUrl("http://baidu.com").build().toUri();
        definition.setUri(uri);

        //定义第一个断言
        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName("Path");

        Map<String, String> predicateParams = new HashMap<>(8);
        predicateParams.put("pattern", "/jd");
        predicate.setArgs(predicateParams);

        //定义Filter
        FilterDefinition filter = new FilterDefinition();
        filter.setName("AddRequestHeader");
        Map<String, String> filterParams = new HashMap<>(8);
        //该_genkey_前缀是固定的，见org.springframework.cloud.gateway.support.NameUtils类
        filterParams.put("_genkey_0", "header");
        filterParams.put("_genkey_1", "addHeader");
        filter.setArgs(filterParams);

        FilterDefinition filter1 = new FilterDefinition();
        filter1.setName("AddRequestParameter");
        Map<String, String> filter1Params = new HashMap<>(8);
        filter1Params.put("_genkey_0", "param");
        filter1Params.put("_genkey_1", "addParam");
        filter1.setArgs(filter1Params);

        definition.setFilters(Arrays.asList(filter, filter1));
        definition.setPredicates(Collections.singletonList(predicate));
        routeDefinitions.add(definition);
        System.out.println(JSON.toJSONString(routeDefinitions));
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }



}
