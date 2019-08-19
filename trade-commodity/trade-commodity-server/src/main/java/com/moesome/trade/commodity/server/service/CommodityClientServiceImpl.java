package com.moesome.trade.commodity.server.service;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.commodity.server.manager.CacheManager;
import com.moesome.trade.commodity.server.model.dao.CommodityMapper;
import com.moesome.trade.commodity.server.model.domain.Commodity;
import com.moesome.trade.commodity.server.util.Transform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommodityClientServiceImpl implements CommodityClientService {
    @Autowired
    public CommodityMapper commodityMapper;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public List<CommodityDetailVo> getByUserIdPagination(Long userId, String order, Integer page) {
        List<Commodity> commodityList = commodityMapper.selectByUserIdPagination(userId, order, (page - 1) * 10, 10);
        return commodityList
                .stream()
                .map(Transform::transformCommodityToCommodityDetailVo)
                .collect(Collectors.toList());
    }

    @Override
    public CommodityDetailVo getByCommodityId(Long commodityId) {
        return cacheManager.getCommodityDetailVo(commodityId);
    }

    @Override
    public boolean decrementStock(Long commodityId) {
        return cacheManager.decrementStock(commodityId);
    }

    @Override
    public void incrementStock(Long commodityId) {
        cacheManager.incrementStock(commodityId);
    }
}
