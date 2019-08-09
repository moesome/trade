package com.moesome.trade.commodity.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.moesome.trade.commodity.server.model.dao")
public class TradeCommodityServerApplication{
    public static void main(String[] args) {
        SpringApplication.run(TradeCommodityServerApplication.class, args);
    }
}
