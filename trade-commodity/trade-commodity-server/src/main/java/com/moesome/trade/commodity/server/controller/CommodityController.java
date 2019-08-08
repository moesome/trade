package com.moesome.trade.commodity.server.controller;

import com.moesome.trade.commodity.server.service.CommodityService;
import com.moesome.trade.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("commodities")
@Slf4j
public class CommodityController {

    @Autowired
    private CommodityService commodityService;

    @GetMapping
    public Result index(Integer page, String order){
        if (page == null || order == null){
            return Result.REQUEST_ERR;
        }
        return commodityService.index(page,order);
    }

    @GetMapping("detail/{id}")
    public Result detail(@PathVariable Long id){
        return commodityService.detail(id);
    }

}