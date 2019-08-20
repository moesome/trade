package com.moesome.trade.notification.server.config;

import com.moesome.trade.common.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class MQConfig {


	// 设置对象转化器
	@Bean
	MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public Queue emailQueue(){
		// 该队列消息不重要，不存放到硬盘里
		return new Queue(QueueConfig.EMAIL_QUEUE,false);
	}

	@Bean
	public DirectExchange orderDirectExchange(){
		return new DirectExchange(QueueConfig.ORDER_DIRECT_EXCHANGE);
	}

	@Bean
	public Binding emailDirectBinding(){
		return BindingBuilder.bind(emailQueue()).to(orderDirectExchange()).with(QueueConfig.EMAIL_QUEUE_KEY);
	}
}

