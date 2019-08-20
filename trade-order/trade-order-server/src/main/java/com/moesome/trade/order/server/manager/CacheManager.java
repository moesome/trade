package com.moesome.trade.order.server.manager;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.order.common.request.CommodityOrderVo;

public interface CacheManager {
    /**
     * 添加订单请求到缓存
     * @param commodityOrderVo
     * @return 如果缓存中不存在则返回 true ，存在则返回 false
     */
    boolean saveCommodityOrderVo(CommodityOrderVo commodityOrderVo);

    /**
     * 取消缓存中的订单请求
     * @param commodityOrderVo
     */
    void removeCommodityOrderVo(CommodityOrderVo commodityOrderVo);

    /**
     * 存入订单状态，一分钟后过期
     * @param userId
     * @param commodityId
     * @param result 1：已完成，-1：处理失败
     */
    void saveCommodityOrderResult(Long userId,Long commodityId,int result);

    /**
     * 取出订单状态
     * @param userId
     * @param commodityId
     * @return 1：已完成，-1：处理失败，0：未处理该订单
     */
    int getCommodityOrderResult(Long userId,Long commodityId);
}
