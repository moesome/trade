package com.moesome.trade.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class TradeEurekaApplication {
	public static void main(String[] args) {
		SpringApplication.run(TradeEurekaApplication.class, args);
	}

}
