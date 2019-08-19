package com.moesome.trade.order.server.util;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.order.common.response.vo.CommodityOrderDetailAndCommodityDetailVo;
import com.moesome.trade.order.server.model.domain.CommodityOrder;

public class Transform {
    public static CommodityOrderDetailAndCommodityDetailVo combineCommodityOrderDetailAndCommodityDetailVo(CommodityOrder commodityOrder, CommodityDetailVo commodityDetailVo){
        CommodityOrderDetailAndCommodityDetailVo combine = new CommodityOrderDetailAndCommodityDetailVo();
        combine.setCommodityOrderCreatedAt(commodityOrder.getCreatedAt());
        combine.setCommodityOrderId(commodityOrder.getCommodityId());
        combine.setCommodityOrderStatus(commodityOrder.getStatus());
        combine.setCommodityDetail(commodityDetailVo.getDetail());
        combine.setCommodityId(commodityDetailVo.getId());
        combine.setCommodityName(commodityDetailVo.getName());
        combine.setCommodityUserId(commodityDetailVo.getUserId());
        return combine;
    }
}
