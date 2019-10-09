package com.alibaba.csp.sentinel.dashboard.controller;

import com.alibaba.csp.sentinel.dashboard.domain.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/test")
    public Result test(){
        System.out.println("test______________________________________");
        return Result.ofSuccess("");
    }

    @RequestMapping(value = "/test/test")
    public Result test1() {
        System.out.println("test/test______________________________________");
        return Result.ofSuccess("");
    }
}
