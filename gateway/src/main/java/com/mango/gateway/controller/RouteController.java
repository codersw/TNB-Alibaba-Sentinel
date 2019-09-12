package com.mango.gateway.controller;

import com.mango.gateway.model.GatewayResponse;
import com.mango.gateway.service.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/routes")
public class RouteController {

    private final Logger logger = LoggerFactory.getLogger(RouteController.class);


    @Autowired
    private RouteService routeService;

    @GetMapping("/list")
    public GatewayResponse list(){
        try {
            return GatewayResponse.builder().data(this.routeService.list()).msg("获取网关成功").success(true).build();
        } catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
            return GatewayResponse.builder().code(-1).msg(e.getMessage()).build();
        }
    }

    @GetMapping("/refresh")
    public GatewayResponse refresh() {
        try {
            this.routeService.notifyChanged();
            return GatewayResponse.builder().msg("刷新成功").success(true).build();
        } catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
            return GatewayResponse.builder().code(-1).msg(e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    public GatewayResponse delete(@PathVariable String id) {
        try {
            this.routeService.delete(id);
            return GatewayResponse.builder().msg("删除成功").success(true).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return GatewayResponse.builder().code(-1).msg(e.getMessage()).build();
        }
    }

    @GetMapping("/{id}")
    public GatewayResponse get(@PathVariable String id) {
        try {
            return GatewayResponse.builder().msg("获取成功").success(true).data(this.routeService.list().stream().filter(r -> id.equals(r.getId()))).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return GatewayResponse.builder().code(-1).msg(e.getMessage()).build();
        }
    }

    @PostMapping("/add")
    public GatewayResponse add(@RequestBody RouteDefinition route) {
        try {
            this.routeService.add(route);
            return GatewayResponse.builder().msg("添加成功").success(true).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return GatewayResponse.builder().code(-1).msg(e.getMessage()).build();
        }
    }

    @PostMapping("/update")
    public GatewayResponse update(@RequestBody RouteDefinition route) {
        try {
            this.routeService.update(route);
            return GatewayResponse.builder().msg("修改成功").success(true).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return GatewayResponse.builder().code(-1).msg(e.toString()).build();
        }
    }
}
