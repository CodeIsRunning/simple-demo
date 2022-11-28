package com.xfliu;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.xfliu.config.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.net.URL;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MultithreadApplicationTests {

    @Resource
    private  RedisUtil redisUtil;


    @Resource
    AmazonS3 amazonS3;

    @Test
    public void contextLoads() {

        //redisUtil.set("test","test");

        List<Bucket> buckets = amazonS3.listBuckets();
        System.out.println("Buckets are:");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }

        try {
            PutObjectResult fileRequest =  amazonS3.putObject("onlinetaxi-tjgjyt", "test6", new File("F:\\b838a5b116a28c5492616e0162eeabba.jpeg"));

            System.out.println(fileRequest.getMetadata().getETag());
            System.out.println(JSONObject.toJSON(fileRequest));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }

        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest("onlinetaxi-tjgjyt", "test5")
                    .withMethod(HttpMethod.GET);
                   // .withExpiration(expiration);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            System.out.println(url);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
    }
    //https://test01-tjgjyt.hb4oss.xstore.ctyun.cn/test?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20221027T074232Z&X-Amz-SignedHeaders=host&X-Amz-Expires=86400&X-Amz-Credential=864nIoOU1Q1aIjv9f99h%2F20221027%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Signature=820306d42e89ef71a1fa0a0c83ab39fbe03a61f6efd564dd76610ecb6f0da2d6

}
