package com.alibaba.csp.sentinel.dashboard.controller.gateway;


import com.alibaba.csp.sentinel.dashboard.auth.AuthService;
import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.RouteDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.service.RouteService;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;

/**
 * 网关规则
 * @author shaowen
 */
@RestController
@RequestMapping(value = "/gateway/routes")
public class GatewayRoutesCotroller {

    private final Logger logger = LoggerFactory.getLogger(GatewayRoutesCotroller.class);

    @Autowired
    private AuthService<HttpServletRequest> authService;

    //@Autowired
    //private RestTemplate restTemplate;

    @Autowired
    private RouteService routeService;

    //@Autowired
    //private SentinelApiClient sentinelApiClient;

    /**
     * 获取列表
     * @param request
     * @param app
     * @param ip
     * @param port
     * @return
     */
    @GetMapping("/list")
    public Result<Object> queryRoutes(HttpServletRequest request, String app, String ip, Integer port) {
        AuthService.AuthUser authUser = authService.getAuthUser(request);
        authUser.authTarget(app, AuthService.PrivilegeType.ALL);

        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isEmpty(ip)) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (port == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        try {
            return Result.ofSuccess(routeService.list(app,ip,port));
        } catch (Throwable throwable) {
            logger.error("query gateway routes error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    /**
     * 添加
     * @param request
     * @param body
     * @return
     */
    @PostMapping("/add")
    public Result<Object> addRoutes(HttpServletRequest request, @RequestBody RouteDefinitionEntity body) {
        AuthService.AuthUser authUser = authService.getAuthUser(request);
        authUser.authTarget(body.getApp(), AuthService.PrivilegeType.ALL);
        if (StringUtil.isEmpty(body.getApp())) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isEmpty(body.getIp())) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (body.getPort() == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        try{
            routeService.add(body);
            return Result.ofSuccess(null);
        } catch (Throwable throwable) {
            logger.error("query gateway routes error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    /**
     * 修改
     * @param request
     * @param body
     * @return
     */
    @PostMapping("/update")
    public Result<Object> updateRoutes(HttpServletRequest request, @RequestBody RouteDefinitionEntity body) {
        AuthService.AuthUser authUser = authService.getAuthUser(request);
        authUser.authTarget(body.getApp(), AuthService.PrivilegeType.ALL);
        if (StringUtil.isEmpty(body.getApp())) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isEmpty(body.getIp())) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (body.getPort() == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        try{
            routeService.update(body);
            return Result.ofSuccess(null);
        } catch (Throwable throwable) {
            logger.error("query gateway routes error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    /**
     * 删除
     * @param request
     * @param app
     * @param ip
     * @param port
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Object> deleteRoutes(HttpServletRequest request, String app, String ip, Integer port, @PathVariable String id) {
        AuthService.AuthUser authUser = authService.getAuthUser(request);
        authUser.authTarget(app, AuthService.PrivilegeType.ALL);
        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isEmpty(ip)) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (port == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        try{
            routeService.delete(app,ip,port,id);
            return Result.ofSuccess(null);
        } catch (Throwable throwable) {
            logger.error("query gateway routes error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }
}
