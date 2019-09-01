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

   

3. 

4. 

## 下单流程

![](https://raw.githubusercontent.com/moesome/projectImages/master/trade/下单架构.png)