package com.xfliu.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    String accessKey = "";
    String secretKey = "";
    String endPoint = "";

    @Bean(name = "amazonS3")
    public AmazonS3 ossconfig() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        //设置client的最大HTTP连接数
        clientConfiguration.setMaxConnections(50);
        //设置Socket层超时时间
        clientConfiguration.setSocketTimeout(50000);
        //设置建立连接的超时时间
        clientConfiguration.setConnectionTimeout(50000);
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                endPoint, Regions.DEFAULT_REGION.getName());
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                //客户端设置
                .withClientConfiguration(clientConfiguration)
                //凭证设置
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                //endpoint设置
                .withEndpointConfiguration(endpointConfiguration)
                .build();
        return s3;
    }


}
