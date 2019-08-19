package com.moesome.trade.commodity.client;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("trade-commodity-server")
@RequestMapping("client")
public interface CommodityClient {
    @GetMapping("getByUserIdPagination")
    List<CommodityDetailVo> getByUserIdPagination(@PathVariable Long userId, @RequestParam String order,@RequestParam Integer page);

    @GetMapping("getByCommodityId")
    CommodityDetailVo getByCommodityId(@RequestParam Long commodityId);

    @PatchMapping("decrementStock")
    boolean decrementStock(@RequestParam Long commodityId);

    @PatchMapping("incrementStock")
    void incrementStock(@RequestParam Long commodityId);
}
