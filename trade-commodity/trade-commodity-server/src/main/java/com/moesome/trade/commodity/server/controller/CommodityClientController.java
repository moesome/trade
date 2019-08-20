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

    @PostMapping("getByCommodityIdList")
    public List<CommodityDetailVo> getByCommodityIdList(@RequestBody List<Long> commodityIdList){
        return commodityClientService.getByCommodityIdList(commodityIdList);
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

    @GetMapping("getByUserId")
    List<CommodityDetailVo> getByUserId(@RequestParam Long userId){
        return commodityClientService.getByUserId(userId);
    }
}
