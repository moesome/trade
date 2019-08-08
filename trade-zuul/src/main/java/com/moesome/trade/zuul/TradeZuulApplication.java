package com.moesome.trade.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableZuulProxy
@EnableConfigurationProperties
public class TradeZuulApplication {
	public static void main(String[] args) {
		SpringApplication.run(TradeZuulApplication.class, args);
	}
}
