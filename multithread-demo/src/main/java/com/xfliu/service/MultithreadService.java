package com.xfliu.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MultithreadService {


    @Async
    public void asy(){
        System.out.println("多线程");
    }

    public void asy1(){
        System.out.println("单线程");
    }
}
