package com.moesome.trade.commodity.server.util;

import com.moesome.trade.commodity.common.request.CommodityStoreVo;
import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.commodity.server.model.domain.Commodity;

public class Transform {
    /**
     * Commodity to CommodityDetailVo
     * @param commodity
     * @return
     */
    public static CommodityDetailVo transformCommodityToCommodityDetailVo(Commodity commodity){
        if (commodity == null){
            return null;
        }
        CommodityDetailVo commodityDetailVo = new CommodityDetailVo();
        commodityDetailVo.setCreatedAt(commodity.getCreatedAt());
        commodityDetailVo.setDetail(commodity.getDetail());
        commodityDetailVo.setEndAt(commodity.getEndAt());
        commodityDetailVo.setId(commodity.getId());
        commodityDetailVo.setName(commodity.getName());
        commodityDetailVo.setPrice(commodity.getPrice());
        commodityDetailVo.setStartAt(commodity.getStartAt());
        commodityDetailVo.setStock(commodity.getStock());
        commodityDetailVo.setUpdatedAt(commodity.getCreatedAt());
        commodityDetailVo.setUserId(commodity.getUserId());
        return commodityDetailVo;
    }

    /**
     * CommodityStoreVo to Commodity
     * @param commodityStoreVo
     * @return
     */
    public static Commodity transformCommodityStoreVoToCommodity(CommodityStoreVo commodityStoreVo){
        if (commodityStoreVo == null){
            return null;
        }
        Commodity commodity = new Commodity();
        commodity.setDetail(commodityStoreVo.getDetail());
        commodity.setEndAt(commodityStoreVo.getEndAt());
        commodity.setName(commodityStoreVo.getName());
        commodity.setPrice(commodityStoreVo.getPrice());
        commodity.setStartAt(commodityStoreVo.getStartAt());
        commodity.setStock(commodityStoreVo.getStock());
        return commodity;
    }
}
