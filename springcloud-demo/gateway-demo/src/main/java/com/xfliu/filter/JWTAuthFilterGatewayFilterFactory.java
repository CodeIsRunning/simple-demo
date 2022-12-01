package com.xfliu.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RefreshScope
public class JWTAuthFilterGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JWTAuthFilterGatewayFilterFactory.Config> {


    //构造函数
    public JWTAuthFilterGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            ServerHttpRequest serverHttpRequest = exchange.getRequest();
            MediaType contentType = serverHttpRequest.getHeaders().getContentType();

            //支付回调接口不需要验签
            String uriPath = serverHttpRequest.getURI().getPath();


            if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType) || MediaType.APPLICATION_JSON_UTF8.equalsTypeAndSubtype(contentType) || MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(contentType)) {

                //添加参数
                exchange.getRequest().mutate().header("", "");

                return chain.filter(exchange);
            } else {

                throw new RuntimeException("gateaway异常");
            }
        };
    }


    //配置类 接收配置参数
    public static class Config {
    }

}
