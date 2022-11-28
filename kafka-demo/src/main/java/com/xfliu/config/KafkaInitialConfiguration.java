package com.xfliu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;

import java.util.List;

@Configuration
public class KafkaInitialConfiguration {
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("testtopic",8, (short) 2 );
    }

    // 如果要修改分区数，只需修改配置值重启项目即可
    // 修改分区数并不会导致数据的丢失，但是分区数只能增大不能减小
    @Bean
    public NewTopic updateTopic() {
        return new NewTopic("testtopic",10, (short) 2 );
    }

    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return (message, exception, consumer) -> {
            System.out.println("消费异常："+message.getPayload());
            return null;
        };
    }

    // 将这个异常处理器的BeanName放到@KafkaListener注解的errorHandler属性里面
    @KafkaListener(topics = {"topic1"},errorHandler = "consumerAwareErrorHandler")
    public void onMessage4(ConsumerRecord<?, ?> record) throws Exception {
        throw new Exception("简单消费-模拟异常");
    }

    // 批量消费也一样，异常处理器的message.getPayload()也可以拿到各条消息的信息
    @KafkaListener(topics = "topic1",errorHandler="consumerAwareErrorHandler")
    public void onMessage5(List<ConsumerRecord<?, ?>> records) throws Exception {
        System.out.println("批量消费一次...");
        throw new Exception("批量消费-模拟异常");
    }
}
