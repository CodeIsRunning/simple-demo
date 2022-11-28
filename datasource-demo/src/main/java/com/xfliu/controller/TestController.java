package com.xfliu.controller;

import com.xfliu.dao.model.Student;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TestController {

    @RequestMapping("test")
    public void test(@RequestBody @Valid Student student){
        System.out.println(student);
    }

}
