package com.moesome.trade.commodity.server.controller;

import com.moesome.trade.commodity.common.request.CommodityStoreVo;
import com.moesome.trade.commodity.common.response.result.CommodityResult;
import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.commodity.server.service.CommodityService;
import com.moesome.trade.commodity.server.service.CommodityServiceImpl;
import com.moesome.trade.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 外部调用
 */
@RestController
@RequestMapping("commodities")
public class CommodityController{

    @Autowired
    private CommodityService commodityService;

    // 主界面和详情
    @GetMapping
    public Result index(Integer page, String order){
        if (page == null || order == null){
            return Result.REQUEST_ERR;
        }
        return commodityService.index(page,order);
    }

    @GetMapping("detail/{commodityId}")
    public Result detail(@PathVariable Long commodityId){
        return commodityService.detail(commodityId);
    }

    // 管理界面
    @GetMapping("manage")
    public Result manage(@RequestHeader("user-id") Long userId, Integer page, String order){
        if (page == null || order == null){
            return Result.REQUEST_ERR;
        }
        return commodityService.manage(userId,order,page);
    }

    @GetMapping("{commodityId}")
    public Result show(@RequestHeader("user-id") Long userId,@PathVariable Long commodityId){
        return commodityService.show(userId,commodityId);
    }

    @PostMapping
    public Result store(@RequestHeader("user-id") Long userId,@RequestBody @Validated CommodityStoreVo commodityStoreVo){
        if (commodityStoreVo.getStartAt().after(commodityStoreVo.getEndAt())){
            return CommodityResult.START_TIME_NOT_ALLOWED;
        }
        return commodityService.store(userId,commodityStoreVo);
    }

    @PatchMapping("{commodityId}")
    public Result update(@RequestHeader("user-id") Long userId, @PathVariable Long commodityId,@RequestBody @Validated CommodityStoreVo commodityStoreVo){
        if (commodityStoreVo.getStartAt().after(commodityStoreVo.getEndAt())){
            return CommodityResult.START_TIME_NOT_ALLOWED;
        }
        return commodityService.update(userId,commodityStoreVo,commodityId);
    }
    // 暂未开放
    //@DeleteMapping("{id}")
    public Result delete(@RequestHeader("user-id") Long userId, @PathVariable Long commodityId){
        return commodityService.delete(userId,commodityId);
    }
}