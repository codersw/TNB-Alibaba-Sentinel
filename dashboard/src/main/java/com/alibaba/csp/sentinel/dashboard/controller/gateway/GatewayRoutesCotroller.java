package com.alibaba.csp.sentinel.dashboard.controller.gateway;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mango.common.FileConsts;
import com.mango.common.FileUtils;
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

    @Autowired
    private RestTemplate restTemplate;

    @Value("${rest.url}")
    private String[] restUrl;

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
            return Result.ofSuccess(JSON.parse(FileUtils.getDatafromFile(SentinelConfig.getConfig("user.home")+ FileConsts.DIR ,FileConsts.GATEWAY_ROUTES)));
        } catch (Throwable throwable) {
            logger.error("query gateway routes error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    @PostMapping(path = {"/add","/update"})
    public Result<Object> addRoutes(HttpServletRequest request, @RequestBody JSONObject body) {
        AuthService.AuthUser authUser = authService.getAuthUser(request);
        authUser.authTarget(body.getString("app"), AuthService.PrivilegeType.ALL);
        if (StringUtil.isEmpty(body.getString("app"))) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isEmpty(body.getString("ip"))) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (StringUtil.isEmpty(body.getString("port"))) {
            return Result.ofFail(-1, "port can't be null");
        }
        try{
            for(String url : restUrl){
                restTemplate.postForObject(url + request.getServletPath().replace("/gateway","" ), body, JSONObject.class);
            }
            return Result.ofSuccess(null);
        } catch (Throwable throwable) {
            logger.error("query gateway routes error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteRoutes(HttpServletRequest request, String app, String ip, Integer port) {
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
            System.out.println(request.getServletPath());
            for(String url : restUrl){
                restTemplate.delete(url + request.getServletPath().replace("/gateway",""));
            }
            return Result.ofSuccess(null);
        } catch (Throwable throwable) {
            logger.error("query gateway routes error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

}
