package com.moesome.trade.order.server;

import com.moesome.trade.commodity.client.CommodityClient;
import com.moesome.trade.user.client.UserClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {CommodityClient.class, UserClient.class})
@MapperScan("com.moesome.trade.order.server.model.dao")
public class TradeOrderServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeOrderServerApplication.class, args);
    }
}
