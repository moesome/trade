package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;

public interface CacheManager {
    void saveCommodityDetailVo(CommodityDetailVo commodityDetailVo);
    void saveCommodityDetailVo(CommodityDetailVo commodityDetailVo,Long expireSecond);
    CommodityDetailVo getCommodityDetailVo(Long spikeId);
    void removeCommodityDetailVo(Long commodityId);
}
