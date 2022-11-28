package com.xfliu.rockmq.config;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.*;

/**
 * 消费消息处理类
 *
 * @author wlyg
 */
@Component
@Slf4j
public class ConsumeMsgProcessor implements MessageListenerConcurrently {

    private static final int RECONSUME_TIMES = 10;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        list.stream().forEach(s->{
            String topic = s.getTopic();
            try {
                String message = new String(s.getBody(), "UTF-8");
                log.info(message);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    @Autowired



    /**
     * 线程池
     */
    static ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(8)
            .setMaxPoolSize(32)
            .setKeepAliveTime(30, TimeUnit.SECONDS)
            .setThreadFactory(ThreadFactoryBuilder.create().setNamePrefix("save_notify").build())
            .setWorkQueue(new LinkedBlockingQueue<>(1000))
            .setHandler(new ThreadPoolExecutor.AbortPolicy())
            .build();

    /**
     * 更新通知表
     *
     * @param transactionId
     */
    private void updateStateForNotify(String transactionId) {
        CompletableFuture.runAsync(() -> {
            try {

            } catch (Exception e) {
                log.error("更新通知表异常{}", transactionId, e);
            }
        }, executor);
    }
}
