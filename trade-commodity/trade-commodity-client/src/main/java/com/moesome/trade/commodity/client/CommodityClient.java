package com.moesome.trade.commodity.client;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("trade-commodity-server")
@RequestMapping("client")
public interface CommodityClient {
    @PostMapping("getByCommodityIdList")
    List<CommodityDetailVo> getByCommodityIdList(@RequestBody List<Long> commodityIdList);

    @GetMapping("getByCommodityId")
    CommodityDetailVo getByCommodityId(@RequestParam Long commodityId);

    @PatchMapping("decrementStock")
    boolean decrementStock(@RequestParam Long commodityId);

    @PatchMapping("incrementStock")
    void incrementStock(@RequestParam Long commodityId);

    @GetMapping("getByUserId")
    List<CommodityDetailVo> getByUserId(@RequestParam Long userId);
}
