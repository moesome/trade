package com.moesome.trade.recharge.server;

import com.moesome.trade.user.client.UserClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {UserClient.class})
@EnableScheduling
@MapperScan("com.moesome.trade.recharge.server.model.dao")
public class TradeRechargeServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeRechargeServerApplication.class, args);
    }

}
