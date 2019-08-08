package com.moesome.trade.commodity.server.service;

import com.moesome.trade.commodity.common.response.result.CommodityResult;
import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.commodity.server.manager.CacheManager;
import com.moesome.trade.commodity.server.manager.DistributedLock;
import com.moesome.trade.commodity.server.model.dao.CommodityMapper;
import com.moesome.trade.commodity.server.model.domain.Commodity;
import com.moesome.trade.common.code.SuccessCode;
import com.moesome.trade.common.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommodityService {
    @Autowired
    private CommodityMapper commodityMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DistributedLock distributedLock;

    public Result index(int page, String order) {
        List<Commodity> commodityList= commodityMapper.selectByPagination(order, (page - 1) * 10, 10);
        Integer count = commodityMapper.count();
        List<CommodityDetailVo> commodityDetailVoList = new ArrayList<>(10);
        for (Commodity commodity : commodityList){
            commodityDetailVoList.add(transformCommodityToUserCommodityDetailVo(commodity));
        }
        return new CommodityResult(SuccessCode.OK,commodityDetailVoList, count);
    }

    public Result detail(Long id) {
        List<CommodityDetailVo> list = new ArrayList<>(1);
        CommodityDetailVo commodityDetailVo = cacheManager.getCommodityDetailVo(id);
        if (commodityDetailVo == null){
            try{
                Boolean lock = distributedLock.lock(id);
                if(lock){
                    commodityDetailVo = cacheManager.getCommodityDetailVo(id);
                    // 双重锁校验防止大量请求查数据库
                    if (commodityDetailVo == null){
                        Commodity commodity = commodityMapper.selectByPrimaryKey(id);
                        commodityDetailVo = transformCommodityToUserCommodityDetailVo(commodity);
                        // 处理缓存穿透，即当查询的 id 为空时，
                        if (commodityDetailVo == null){
                            commodityDetailVo = new CommodityDetailVo();
                            commodityDetailVo.setId(id);
                            commodityDetailVo.setStock(Integer.MIN_VALUE);
                        }
                        // 10 秒即过期
                        cacheManager.saveCommodityDetailVo(commodityDetailVo,10L);
                    }
                }
            }finally {
                distributedLock.unlock(id);
            }
        }
        if (commodityDetailVo == null || commodityDetailVo.getStock() == Integer.MIN_VALUE){
            // 发生缓存穿透
            return Result.REQUEST_ERR;
        }
        list.add(commodityDetailVo);
        return new CommodityResult(SuccessCode.OK,list,1);
    }

    private CommodityDetailVo transformCommodityToUserCommodityDetailVo(Commodity commodity){
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
}
