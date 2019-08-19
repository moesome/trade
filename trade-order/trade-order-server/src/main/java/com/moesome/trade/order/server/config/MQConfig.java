package com.moesome.trade.order.server.config;

import com.moesome.trade.common.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
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
	public Queue stockDecrementQueue(){
		return new Queue(QueueConfig.STOCK_DECREMENT_QUEUE,true);
	}

	@Bean
	public Queue stockRollbackQueue(){
		return new Queue(QueueConfig.STOCK_ROLLBACK_QUEUE,true);
	}


	@Bean
	public DirectExchange orderDirectExchange(){
		return new DirectExchange(QueueConfig.ORDER_DIRECT_EXCHANGE);
	}

	@Bean
	public Binding stockDecrementDirectBinding(){
		return BindingBuilder.bind(stockDecrementQueue()).to(orderDirectExchange()).with(QueueConfig.STOCK_DECREMENT_QUEUE_KEY);
	}

	@Bean
	public Binding stockRollbackDirectBinding(){
		return BindingBuilder.bind(stockRollbackQueue()).to(orderDirectExchange()).with(QueueConfig.STOCK_ROLLBACK_QUEUE_KEY);
	}

}

