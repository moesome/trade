package com.moesome.trade.commodity.server.controller;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.commodity.server.service.CommodityClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("client")
public class CommodityClientController {
    @Autowired
    private CommodityClientService commodityClientService;

    @GetMapping("getByUserIdPagination")
    public List<CommodityDetailVo> getByUserIdPagination( Long userId, String order, Integer page){
        return commodityClientService.getByUserIdPagination(userId,order,page);
    }

    @GetMapping("getByCommodityId")
    public CommodityDetailVo getByCommodityId(Long commodityId){
        return commodityClientService.getByCommodityId(commodityId);
    }

    @PatchMapping("decrementStock")
    public boolean decrementStock(Long commodityId){
        return commodityClientService.decrementStock(commodityId);
    }

    @PatchMapping("incrementStock")
    public void incrementStock(Long commodityId){
        commodityClientService.incrementStock(commodityId);
    }
}
