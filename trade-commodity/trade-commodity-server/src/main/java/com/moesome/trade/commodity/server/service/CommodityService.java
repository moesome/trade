package com.moesome.trade.commodity.server.service;

import com.moesome.trade.commodity.common.request.CommodityStoreVo;
import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.common.response.Result;

public interface CommodityService {
    // 商品主界面和详情
    Result index(int page, String order);
    Result detail(Long commodityId);

    // 商品管理
    // 管理界面主界面
    Result manage(Long userId, String order, Integer page);

    Result show(Long userId, Long commodityId);

    Result store(Long userId, CommodityStoreVo commodityStoreVo);

    Result update(Long userId, CommodityStoreVo commodityStoreVo, Long commodityId);

    Result delete(Long userId, Long commodityId);
}
