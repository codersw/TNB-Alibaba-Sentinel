package com.mango.gateway.controller;

import com.mango.gateway.model.GatewayResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
public class TestController {

    @RequestMapping(value = "/test1")
    public GatewayResponse test(){
        return GatewayResponse.builder().build();
    }

    public static void main(String[] args) {
        Function<List<Integer>, List<Integer>> name = e -> {
            System.out.println(0);
            e.add(0);
            return e;
        };
        //System.out.println("apply value=" + name.apply(new ArrayList<>()));
        Function<List<Integer>, List<Integer>> name1 = e -> {
            System.out.println(1);
            e.add(1);
            return e;
        };
        //返回一个先执行当前函数对象apply方法再执行after函数对象apply方法的函数对象。
        //System.out.println("andThen value=" + name.andThen(name1).apply(new ArrayList<>()));
        //返回一个先执行before函数对象apply方法再执行当前函数对象apply方法的函数对象
        //System.out.println("compose value=" + name.compose(name1).apply(new ArrayList<>()));
        System.out.println(""+Function.identity());
    }
}
