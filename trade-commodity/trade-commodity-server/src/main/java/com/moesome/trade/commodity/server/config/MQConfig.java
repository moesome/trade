package com.moesome.trade.commodity.server.config;

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
	public Queue coinDecrementQueue(){
		return new Queue(QueueConfig.COIN_DECREMENT_QUEUE,true);
	}

	@Bean
	public Queue orderFailedQueue(){
		return new Queue(QueueConfig.ORDER_FAILED_QUEUE,true);
	}

	@Bean
	public DirectExchange orderDirectExchange(){
		return new DirectExchange(QueueConfig.ORDER_DIRECT_EXCHANGE);
	}

	@Bean
	public Binding coinDecrementDirectBinding(){
		return BindingBuilder.bind(coinDecrementQueue()).to(orderDirectExchange()).with(QueueConfig.COIN_DECREMENT_QUEUE_KEY);
	}

	@Bean
	public Binding orderFailedDirectBinding(){
		return BindingBuilder.bind(orderFailedQueue()).to(orderDirectExchange()).with(QueueConfig.ORDER_FAILED_QUEUE_KEY);
	}

}

