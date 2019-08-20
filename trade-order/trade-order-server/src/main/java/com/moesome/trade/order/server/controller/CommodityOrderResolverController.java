package com.moesome.trade.order.server.controller;

import com.moesome.trade.common.response.Result;
import com.moesome.trade.order.server.service.CommodityOrderResolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("commodity_orders")
public class CommodityOrderResolverController {
    @Autowired
    private CommodityOrderResolverService commodityOrderResolverService;

    @PatchMapping("sends/remind/{commodityOrderId}")
    public Result remindToSendProduction(@RequestHeader("user-id") Long userId, @PathVariable Long commodityOrderId){
        return commodityOrderResolverService.remindToSendProduction(userId, commodityOrderId);
    }

    @PatchMapping("sends/received/{commodityOrderId}")
    public Result receivedProduction(@RequestHeader("user-id") Long userId, @PathVariable Long commodityOrderId){
        return commodityOrderResolverService.receivedProduction(userId, commodityOrderId);
    }

    @PatchMapping("sends/{commodityOrderId}")
    public Result sendProduction(@RequestHeader("user-id") Long userId, @PathVariable Long commodityOrderId){
        return commodityOrderResolverService.sendProduction(userId, commodityOrderId);
    }

    // 获取该用户创建商品的被秒杀订单，连同商品信息返回（分页）
    @GetMapping("sends")
    public Result index(int page, String order,@RequestHeader("user-id") Long userId){
        return commodityOrderResolverService.index(page,order,userId);
    }
}
