package com.xfliu.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 这个过滤器解决body不能重复读的问题
 * @author wlyg
 */
@Component
public class CacheBodyGlobalFilter implements Ordered, GlobalFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (exchange.getRequest().getHeaders().getContentType() == null) {
			return chain.filter(exchange);
		} else {
			return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
				byte[] bytes = new byte[dataBuffer.readableByteCount()];
				dataBuffer.read(bytes);
				// 释放堆外内存
				DataBufferUtils.release(dataBuffer);

				ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
					@Override
					public Flux<DataBuffer> getBody() {
						return Flux.defer(() -> {
							DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
							DataBufferUtils.retain(buffer);
							return Mono.just(buffer);
						});
					}
				};
				return chain.filter(exchange.mutate().request(mutatedRequest).build());

			});
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}