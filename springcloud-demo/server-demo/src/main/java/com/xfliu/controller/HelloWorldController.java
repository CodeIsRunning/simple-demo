package com.xfliu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("user")
public class HelloWorldController {

    @RequestMapping("hello")
    public String hello(){
        return "hello";
    }

}
