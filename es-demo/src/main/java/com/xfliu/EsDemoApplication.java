package com.xfliu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsDemoApplication.class, args);
    }

}
