package com.moesome.trade.user.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.moesome.trade.user.server.model.dao")
public class TradeUserServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeUserServerApplication.class, args);
    }
}
