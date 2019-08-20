package com.moesome.trade.order.server.controller;

import com.moesome.trade.common.response.Result;
import com.moesome.trade.order.common.request.CommodityOrderVo;
import com.moesome.trade.order.server.manager.CacheManager;
import com.moesome.trade.order.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("commodity_orders")
public class CommodityOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result index(@RequestHeader("user-id") Long userId, Integer page, String order){
        if (page == null || order == null){
            return Result.REQUEST_ERR;
        }
        return orderService.index(userId,order,page);
    }

    // 此处传入的 pojo 仅仅接收秒杀 id 即可，无需校验用户名是否为空，用户名在 session 内提取
    @PatchMapping
    public Result store(@RequestHeader("user-id") Long userId, @RequestBody @Validated CommodityOrderVo commodityOrderVo){
        commodityOrderVo.setUserId(userId);
        return orderService.store(commodityOrderVo);
    }

    @GetMapping("check/{commodityId}")
    public Result check(@RequestHeader("user-id") Long userId, @PathVariable Long commodityId){
        return orderService.check(userId,commodityId);
    }

    //	@DeleteMapping("{id}")
    public Result delete(@RequestHeader("user-id") Long userId, @PathVariable Long id, CommodityOrderVo commodityOrderVo){
        return orderService.delete(userId,id, commodityOrderVo.getCommodityId());
    }
}
