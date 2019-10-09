package com.mango.gateway.controller;

import com.mango.gateway.model.GatewayResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/test1")
    public GatewayResponse test(){
        return GatewayResponse.builder().build();
    }
}
