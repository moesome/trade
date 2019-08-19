package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;

public interface CacheManager {
    /**
     * 存入商品信息，永久不过期
     * @param commodityDetailVo
     */
    void saveCommodityDetailVo(CommodityDetailVo commodityDetailVo);

    /**
     * 存入商品信息，设置过期时间
     * @param commodityDetailVo
     * @param expireSecond
     */
    void saveCommodityDetailVo(CommodityDetailVo commodityDetailVo,Long expireSecond);

    /**
     * 取出商品部分信息（price,startAt,endAt），已经处理了缓存失效的情况
     * 返回 null 表示缓存发生缓存穿透
     * 返回空 CommodityDetailVo 对象表示缓存中查到了防止缓存穿透的临时值
     * @param commodityId
     * @return
     */
    CommodityDetailVo getCommodityDetailVo(Long commodityId);

    void removeCommodityDetailVo(Long commodityId);


    boolean decrementStock(Long commodityId);

    void incrementStock(Long commodityId);
}
