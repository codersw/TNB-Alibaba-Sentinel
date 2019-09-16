package com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class RouteDefinitionEntity {

    private String id;
    private String app;
    private String ip;
    private Integer port;
    private URI uri;
    private int order = 0;

    private List<Object> predicates = new ArrayList<>();

    private List<Object> filters = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Object> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<Object> predicates) {
        this.predicates = predicates;
    }

    public List<Object> getFilters() {
        return filters;
    }

    public void setFilters(List<Object> filters) {
        this.filters = filters;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
