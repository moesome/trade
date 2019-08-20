package com.moesome.trade.recharge.server.model.dao;

import com.moesome.trade.recharge.server.model.domain.Recharge;

import java.util.List;

public interface RechargeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Recharge record);

    int insertSelective(Recharge record);

    Recharge selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Recharge record);

    int updateByPrimaryKey(Recharge record);

    List<Recharge> selectAllUnResolver();
}