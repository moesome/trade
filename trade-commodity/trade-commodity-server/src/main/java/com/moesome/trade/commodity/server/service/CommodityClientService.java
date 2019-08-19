package com.moesome.trade.commodity.server.service;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;

import java.util.List;

public interface CommodityClientService {
    /**
     * 根据用户 id 返回该用户创建的商品页面
     * @param userId
     * @param order
     * @param page
     * @return
     */
    List<CommodityDetailVo> getByUserIdPagination(Long userId, String order, Integer page);

    /**
     * 根据商品 id 查出商品信息，先从缓存查，若缓存为空则从数据库查并刷新缓存。
     * @param commodityId
     * @return null 表示发生缓存穿透
     */
    CommodityDetailVo getByCommodityId(Long commodityId);

    /**
     * 根据商品 id 减少商品缓存库存
     * @param commodityId
     * @return 如果减少成功则返回 true
     */
    boolean decrementStock(Long commodityId);

    /**
     * 根据商品 id 增加商品缓存库存
     * @param commodityId
     */
    void incrementStock(Long commodityId);
}
