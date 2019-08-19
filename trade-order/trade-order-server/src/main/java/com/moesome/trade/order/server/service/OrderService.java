package com.moesome.trade.order.server.service;

import com.moesome.trade.common.response.Result;
import com.moesome.trade.order.common.request.CommodityOrderVo;

public interface OrderService {
    /**
     * 订单详情页面
     * @param userId
     * @param order
     * @param page
     * @return
     */
    Result index(Long userId, String order, Integer page);

    /**
     * 下订单
     * @param commodityOrderVo
     * @return
     */
    Result store(CommodityOrderVo commodityOrderVo);

    /**
     * 处理客户端轮询检测订单状态
     * @param userId
     * @param spikeId
     * @return
     */
    Result check(Long userId, Long spikeId);

    /**
     * 删除订单，暂未开放入口
     * @param userId
     * @param id
     * @param spikeId
     * @return
     */
    Result delete(Long userId, Long id, Long spikeId);
}
