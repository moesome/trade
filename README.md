# 分布式交易平台

 <p align="center">
     <a href="https://github.com/moesome/trade-web">
     	<img src="https://img.shields.io/badge/%E4%BA%A4%E6%98%93%E5%B9%B3%E5%8F%B0%E5%89%8D%E7%AB%AF-1.0-green">
     </a>
     <img src="https://img.shields.io/badge/SpringBoot-2.1.6.RELEASE-brightgreen">
     <img src="https://img.shields.io/badge/SpringCloud-Greenwich.SR2-blue">
 </p>

## 概述

分布式交易平台是一个供用户间进行交易的平台，用户可以在这里发布商品，其他用户可以对商品进行秒杀。

## 项目简介

本项目是[闲置交易平台](https://github.com/moesome/spike)的分布式版本，将服务根据功能进行了拆分，拆分为了五个主要的微服务：用户服务，商品服务，订单服务，充值服务和通知服务。并将通用功能抽离到共享的模块中。

- 使用 SpringBoot 构建各服务
- 使用 SpringCloud 进行服务间通信
- 使用 MySql 存储数据
- 使用 Redis 缓存数据
- 使用 RabbitMQ 作为消息中间件在服务间异步通信

## 服务间调用架构

![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E9%A1%B9%E7%9B%AE%E6%9E%B6%E6%9E%84.png)

## 操作演示

1. 注册账号
![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/注册.png)
2. 登录
![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/登录.png)
3. 发布商品
    ![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/发布商品1.1.png)
    
    ![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E5%8F%91%E5%B8%83%E5%95%86%E5%93%81%E5%92%8C.png)
4. 秒杀商品

	![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E8%B4%AD%E4%B9%B0.png)
	下单后将排队处理订单
    ![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E7%A7%92%E6%9D%801.png)
    秒杀成功
    ![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E7%A7%92%E6%9D%802.png)
    重复下单
    ![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E7%A7%92%E6%9D%803.png)
5. 发货
	![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E5%8F%91%E8%B4%A7.png)
6. 收货
	![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E6%94%B6%E8%B4%A7.png)
7. 充值
	![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/%E5%85%85%E5%80%BC.png)
## 下单流程

使用消息驱动的模式，处理订单时将各自服务完成后的消息发送到队列，由下一个处理消息的服务取出，继续进行处理。利用 RabbitMQ ACK 机制和持久化机制确保生产者消息一定能投递到队列，消费者开启手动确认，在业务处理完成后发送确认消息，同时使用消息表来记录每个服务已处理的信息，保证消息处理幂等。在某个服务出现异常时向订单失败队列投递消息，由订单服务对已处理的内容进行回滚。保证该流程的最终一致性。

![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/下单架构.png)