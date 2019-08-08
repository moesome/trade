package com.moesome.trade.commodity.server.model.dao;

import com.moesome.trade.commodity.server.model.domain.Commodity;

import java.util.List;

public interface CommodityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Commodity record);

    int insertSelective(Commodity record);

    Commodity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Commodity record);

    int updateByPrimaryKey(Commodity record);

    List<Commodity> selectByPagination(String order, int start, int count);

    Integer count();
}