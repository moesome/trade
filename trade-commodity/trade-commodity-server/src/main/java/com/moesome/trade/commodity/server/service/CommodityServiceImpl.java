package com.moesome.trade.commodity.server.service;

import com.moesome.trade.commodity.common.request.CommodityStoreVo;
import com.moesome.trade.commodity.common.response.result.CommodityResult;
import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.commodity.server.manager.CacheManager;
import com.moesome.trade.commodity.server.manager.DistributedLock;
import com.moesome.trade.commodity.server.model.dao.CommodityMapper;
import com.moesome.trade.commodity.server.model.domain.Commodity;
import com.moesome.trade.common.code.SuccessCode;
import com.moesome.trade.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommodityServiceImpl implements CommodityService, InitializingBean {
    @Autowired
    private CommodityMapper commodityMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public Result index(int page, String order) {
        List<Commodity> commodityList= commodityMapper.selectByPagination(order, (page - 1) * 10, 10);
        Integer count = commodityMapper.count();
        List<CommodityDetailVo> commodityDetailVoList = commodityList
                .stream()
                .map(this::transformCommodityToCommodityDetailVo)
                .collect(Collectors.toList());
        return new CommodityResult(SuccessCode.OK,commodityDetailVoList, count);
    }

    @Override
    public Result detail(Long commodityId) {
        List<CommodityDetailVo> list = new ArrayList<>(1);
        CommodityDetailVo commodityDetailVo = cacheManager.getCommodityDetailVo(commodityId);
        if (commodityDetailVo == null){
            try{
                Boolean lock = distributedLock.lock(commodityId);
                if(lock){
                    commodityDetailVo = cacheManager.getCommodityDetailVo(commodityId);
                    // 双重锁校验防止大量请求查数据库
                    if (commodityDetailVo == null){
                        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);
                        commodityDetailVo = transformCommodityToCommodityDetailVo(commodity);
                        // 处理缓存穿透，即当查询的 id 为空时，
                        if (commodityDetailVo == null){
                            commodityDetailVo = new CommodityDetailVo();
                            commodityDetailVo.setId(commodityId);
                            commodityDetailVo.setStock(Integer.MIN_VALUE);
                            log.warn("缓存穿透 commodityId: "+commodityId);
                            // 10 秒即过期
                            cacheManager.saveCommodityDetailVo(commodityDetailVo,10L);
                        }else{
                            log.info("缓存 commodityId: "+commodityId+" 失效，已刷新");
                            cacheManager.saveCommodityDetailVo(commodityDetailVo);
                        }
                    }
                }
            }finally {
                distributedLock.unlock(commodityId);
            }
        }
        if (commodityDetailVo == null || commodityDetailVo.getStock() == Integer.MIN_VALUE){
            // 发生缓存穿透
            return Result.REQUEST_ERR;
        }
        list.add(commodityDetailVo);
        return new CommodityResult(SuccessCode.OK,list,1);
    }

    /**
     * Commodity to CommodityDetailVo
     * @param commodity
     * @return
     */
    private CommodityDetailVo transformCommodityToCommodityDetailVo(Commodity commodity){
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
    private Commodity transformCommodityStoreVoToCommodity(CommodityStoreVo commodityStoreVo){
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

    @Override
    public Result manage(Long userId, String order, Integer page) {
        List<Commodity> commodityList = commodityMapper.selectByUserIdPagination(userId,order, (page - 1) * 10, 10);
        Integer count = commodityMapper.countByUserId();
        List<CommodityDetailVo> commodityDetailVoList = commodityList
                .stream()
                .map(this::transformCommodityToCommodityDetailVo)
                .collect(Collectors.toList());
        return new CommodityResult(SuccessCode.OK,commodityDetailVoList, count);
    }

    @Override
    public Result show(Long userId, Long commodityId) {
        List<CommodityDetailVo> commodityDetailVoList = new ArrayList<>(1);
        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);// 从数据库中取出
        if (commodity != null && commodity.getUserId().equals(userId)){ // 校验取出的数据用户是否拥有
            commodityDetailVoList.add(transformCommodityToCommodityDetailVo(commodity));
            return new CommodityResult(SuccessCode.OK,commodityDetailVoList,1);
        }else{
            return Result.AUTHORIZED_ERR;
        }
    }

    @Override
    public Result store(Long userId, CommodityStoreVo commodityStoreVo) {
        Commodity commodity = transformCommodityStoreVoToCommodity(commodityStoreVo);
        Date now = new Date();
        commodity.setUserId(userId);
        commodity.setCreatedAt(now);
        commodity.setUpdatedAt(now);
        // id 将在这一步之后插入
        log.info("商品存入数据库: "+commodity);
        commodityMapper.insert(commodity);
        CommodityDetailVo commodityDetailVoAfterInsert = transformCommodityToCommodityDetailVo(commodity);
        log.info("商品写入缓存: "+commodityDetailVoAfterInsert);
        cacheManager.saveCommodityDetailVo(commodityDetailVoAfterInsert);
        return Result.SUCCESS;
    }

    @Override
    public Result update(Long userId, CommodityStoreVo commodityStoreVo, Long commodityId) {
        // 从数据库中根据传入的 commodityId 取出
        Commodity commodityInDB = commodityMapper.selectByPrimaryKey(commodityId);
        Date now = new Date();
        // 开始秒杀后禁止修改
        if (now.after(commodityInDB.getStartAt())){
            return CommodityResult.CANT_MODIFY_AFTER_START;
        }
        // 校验该 spike 用户是否拥有
        if (commodityInDB.getUserId().equals(userId)){
            // 如果拥有才能执行更新
            Commodity commodity = transformCommodityStoreVoToCommodity(commodityStoreVo);
            commodity.setId(commodityId);
            commodity.setUpdatedAt(new Date());
            log.info("以非空商品字段更新数据库: "+commodity);
            commodityMapper.updateByPrimaryKeySelective(commodity);
            // 填充数据，使得 storeVo 转化为 detailVo 避免再次查数据库
            commodity.setUserId(userId);
            commodity.setCreatedAt(commodityInDB.getCreatedAt());
            CommodityDetailVo commodityDetailVo = transformCommodityToCommodityDetailVo(commodity);
            log.info("修该商品写入缓存: "+commodityDetailVo);
            cacheManager.saveCommodityDetailVo(commodityDetailVo);
            return Result.SUCCESS;
        }else{
            log.warn("user: "+userId+"尝试其他用户的商品 commodityId: "+" "+commodityId+" 已阻止");
            return Result.AUTHORIZED_ERR;
        }
    }

    @Override
    public Result delete(Long userId, Long commodityId) {
        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);// 从数据库中取出
        if (commodity != null && commodity.getUserId().equals(commodity.getId())){ // 校验取出的数据用户是否拥有
            // 删除数据库
            log.info("删除商品 commodityId: "+commodityId);
            commodityMapper.deleteByPrimaryKey(commodityId);
            // 删除 redis
            log.info("清除商品库存 commodityId: "+commodityId);
            cacheManager.removeCommodityDetailVo(commodityId);
            return Result.SUCCESS;
        }else{
            log.warn("user: "+userId+"尝试删除其他用户的商品 commodityId: "+" "+commodityId+" 已阻止");
            return Result.AUTHORIZED_ERR;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Commodity> commodityList = commodityMapper.selectAll();
        List<CommodityDetailVo> commodityDetailVoList = commodityList
                .stream()
                .map(this::transformCommodityToCommodityDetailVo)
                .collect(Collectors.toList());
        for (CommodityDetailVo commodityDetailVo : commodityDetailVoList){
            // 缓存秒杀验证数据
            log.info("cache: "+commodityDetailVo);
            cacheManager.saveCommodityDetailVo(commodityDetailVo);
        }
    }
}
