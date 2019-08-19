package com.moesome.trade.order.server.model.dao;

import com.moesome.trade.order.common.response.vo.CommodityOrderDetailAndCommodityDetailVo;
import com.moesome.trade.order.server.model.domain.CommodityOrder;

import java.util.List;

public interface CommodityOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CommodityOrder record);

    int insertSelective(CommodityOrder record);

    CommodityOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CommodityOrder record);

    int updateByPrimaryKey(CommodityOrder record);

    List<CommodityOrder> selectByUserId(Long userId);
//    List<CommodityOrderDetailAndCommodityDetailVo> selectCommodityOrderDetailAndCommodityDetailVoByUserIdPagination(Long userId, String order, int start, int count);

    Integer countByUserId(Long userId);
}