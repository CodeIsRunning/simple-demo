package com.xfliu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MultithreadApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultithreadApplication.class, args);
    }

}
