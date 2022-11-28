package com.xfliu.rockmq.config;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 生产者配置类
 * @author wlyg
 */
@Configuration
@ConditionalOnExpression("'true'.equals('${rocketmq.producer.enabled}')")
@Slf4j
public class RocketMqProducer {

	/**
	 * 生产者组
	 */
	@Value("${rocketmq.producer.groupName:}")
	private String groupName;
	
	/**
	 * mq的nameserver地址
	 */
	@Value("${rocketmq.producer.namesrvAddr:}")
	private String namesrvAddr;

	/**
	 * 消息最大大小，默认4M
	 */
	@Value("${rocketmq.producer.maxMessageSize:4096}")
	private Integer maxMessageSize;
	
	/**
	 * 消息发送超时时间，默认3秒
	 */
	@Value("${rocketmq.producer.sendMsgTimeout:6000}")
	private Integer sendMsgTimeout;
	
	/**
	 * 消息发送失败重试次数，默认2次
	 */
	@Value("${rocketmq.producer.retryTimesWhenSendFailed:2}")
	private Integer retryTimesWhenSendFailed;

	@Value("${rocketmq.producer.enabled:false}")
	private boolean enabled;

	@Bean
	public DefaultMQProducer getRocketMQProducer() throws Exception {
		if (!enabled) {
			return null;
		}

		if (StrUtil.isBlank(this.groupName)) {
			throw new Exception();
		}

		if (StrUtil.isBlank(this.namesrvAddr)) {
			throw new Exception();
		}

		DefaultMQProducer producer;
		producer = new DefaultMQProducer(this.groupName);


		producer.setNamesrvAddr(this.namesrvAddr);
		producer.setCreateTopicKey("AUTO_CREATE_TOPIC_KEY");


		if (this.maxMessageSize != null) {
			producer.setMaxMessageSize(this.maxMessageSize);
		}
		if (this.sendMsgTimeout != null) {
			producer.setSendMsgTimeout(this.sendMsgTimeout);
		}
		// 如果发送消息失败，设置重试次数，默认为2次
		if (this.retryTimesWhenSendFailed != null) {
			producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
		}

		try {
			producer.start();
			log.info(String.format(">>>>producer is start ! groupName:[%s],namesrvAddr:[%s]", this.groupName, this.namesrvAddr));
		} catch (MQClientException e) {
			log.error("生产者异常", e);
			throw new Exception(e);
		}
		return producer;
	}

}
