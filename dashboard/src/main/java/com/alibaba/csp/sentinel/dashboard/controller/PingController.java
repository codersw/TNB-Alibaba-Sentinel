package com.alibaba.csp.sentinel.dashboard.controller;


import com.alibaba.csp.sentinel.dashboard.domain.Result;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;

@RestController
public class PingController {

    private final Logger logger = LoggerFactory.getLogger(PingController.class);

    @RequestMapping(method = {RequestMethod.GET,RequestMethod.POST},value = "/ping")
    public Result list(HttpServletRequest request){
        logger.info(getIpAddress(request)+ "初始化客户端成功");
        return Result.ofSuccess("").setMsg(getIpAddress(request) + "初始化客户端成功").setSuccess(true);
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
