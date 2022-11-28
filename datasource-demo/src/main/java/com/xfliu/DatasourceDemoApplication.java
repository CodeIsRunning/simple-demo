package com.xfliu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.com.xfliu.dao.mapper")
public class DatasourceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatasourceDemoApplication.class, args);
    }

}
