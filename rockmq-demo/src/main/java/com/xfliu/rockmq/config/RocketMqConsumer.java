package com.xfliu.rockmq.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 消费者配置类
 * @author wlyg
 */
@Configuration
@ConditionalOnExpression("${rocketmq.consumer.enabled:false} && !''.equals('${rocketmq.consumer.consumerBeanId:}')")
@Slf4j
public class RocketMqConsumer implements ApplicationContextAware {

	/**
	 * 消费者组
	 */
	@Value("${rocketmq.consumer.groupName:''}")
	private String groupName;
	
	/**
	 * mq的nameserver地址
	 */
	@Value("${rocketmq.consumer.namesrvAddr:}")
	private String namesrvAddr;
	
	/**
	 * mq的消费者最小线程数
	 */
	 @Value("${rocketmq.consumer.consumeThreadMin:5}")
	private int consumeThreadMin;
	
	/**
	 * mq的消费者最大线程数
	 */
	@Value("${rocketmq.consumer.consumeThreadMax:64}")
	private int consumeThreadMax;
	
	/**
	 * mq的消费者主题
	 */
	@Value("${rocketmq.consumer.topics:}")
	private String topics;
	
	/**
	 * 设置一次消费消息的条数，默认为1条
	 */
	@Value("${rocketmq.consumer.consumeMessageBatchMaxSize:1}")
	private int consumeMessageBatchMaxSize;

	@Value("${rocketmq.consumer.enabled:false}")
	private boolean enabled;

	@Value(value = "${rocketmq.consumer.consumerBeanId:}")
	private String consumerBeanId;

	private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
	@Bean
	public DefaultMQPushConsumer getRocketMQConsumer() throws Exception {
		if (!enabled || StrUtil.isBlank(consumerBeanId)) {
			return null;
		}

		if (StrUtil.isBlank(groupName)) {
			throw new Exception();
		}
		if (StrUtil.isBlank(namesrvAddr)) {
			throw new Exception();
		}
		if (StrUtil.isBlank(topics)) {
			throw new Exception();
		}

		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
		consumer.setNamesrvAddr(namesrvAddr);
		consumer.setConsumeThreadMin(consumeThreadMin);
		consumer.setConsumeThreadMax(consumeThreadMax);
		MessageListenerConcurrently consumeMsgProcessor = null;
		try {
			consumeMsgProcessor =
					(MessageListenerConcurrently) applicationContext.getBean(consumerBeanId);
			if (ObjectUtil.isEmpty(consumeMsgProcessor)) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
        consumer.registerMessageListener(consumeMsgProcessor);

		/**
		 * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费 如果非第一次启动，那么按照上次消费的位置继续消费
		 */
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		
		/**
		 * 设置一次消费消息的条数，默认为1条
		 */
		consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);

		try {
			/**
        	 * 设置该消费者订阅的主题和tag，如果是订阅该主题下的所有tag，则tag使用*；
        	 * 如果需要指定订阅该主题下的某些tag，则使用||分割，例如tag1||tag2||tag3
        	 */
        	String[] topicTagsArr = topics.split(";");
        	for (String topicTags : topicTagsArr) {
        		String[] topicTag = topicTags.split("~");
        		consumer.subscribe(topicTag[0],topicTag[1]);
			}

			consumer.start();
			log.info(String.format("<<<<<consumer is start ! groupName:[%s],namesrvAddr:[%s]", this.groupName, this.namesrvAddr));
		} catch (Exception e) {
			log.error("消费者异常", e);
			throw new Exception(e);
		}
		return consumer;
	}

}
