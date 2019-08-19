package com.moesome.trade.order.server.service;

import com.moesome.trade.commodity.client.CommodityClient;
import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.common.code.SuccessCode;
import com.moesome.trade.common.message.CommodityOrderMessage;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.order.common.request.CommodityOrderVo;
import com.moesome.trade.order.common.response.result.CommodityOrderResult;
import com.moesome.trade.order.common.response.vo.CommodityOrderDetailAndCommodityDetailVo;
import com.moesome.trade.order.server.manager.CacheManager;
import com.moesome.trade.order.server.manager.MqSenderManager;
import com.moesome.trade.order.server.model.dao.CommodityOrderMapper;
import com.moesome.trade.order.server.model.domain.CommodityOrder;
import com.moesome.trade.order.server.util.Transform;
import com.moesome.trade.user.client.UserClient;
import com.moesome.trade.user.common.response.vo.UserDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CommodityOrderMapper commodityOrderMapper;

    @Autowired
    private CommodityClient commodityClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MqSenderManager mqSenderManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Result index(Long userId, String order, Integer page) {
//        List<CommodityOrderDetailAndCommodityDetailVo> commodityDetailVos = commodityOrderMapper.selectCommodityOrderDetailAndCommodityDetailVoByUserIdPagination(userId,order, (page - 1) * 10, 10);
        List<CommodityOrder> commodityOrders = commodityOrderMapper.selectByUserId(userId);
        List<CommodityDetailVo> commodityDetailVos = commodityClient.getByUserIdPagination(userId, order, page);
        Integer count = commodityOrderMapper.countByUserId(userId);
        List<CommodityOrderDetailAndCommodityDetailVo> result = new ArrayList<>(10);
        Iterator<CommodityOrder> commodityOrderIterator = commodityOrders.iterator();
        Iterator<CommodityDetailVo> commodityDetailVoIterator = commodityDetailVos.iterator();
        while (commodityOrderIterator.hasNext()){
            CommodityOrder commodityOrder = commodityOrderIterator.next();
            CommodityDetailVo commodityDetailVo = commodityDetailVoIterator.next();
            result.add(Transform.combineCommodityOrderDetailAndCommodityDetailVo(commodityOrder,commodityDetailVo));
        }
        return new CommodityOrderResult(SuccessCode.OK,result,count);
    }

    @Override
    public Result store(CommodityOrderVo commodityOrderVo) {
        // 防止多次重复下单，往缓存中写入订单，可能存在已存在订单但缓存中不存在情况
        if (!cacheManager.saveCommodityOrderVo(commodityOrderVo)){
            return CommodityOrderResult.REPEATED_REQUEST;
        }
        // 取出缓存中的商品信息
        CommodityDetailVo commodityDetailVo = commodityClient.getByCommodityId(commodityOrderVo.getCommodityId());
        if (commodityDetailVo == null){
            // 发生缓存穿透
            return Result.REQUEST_ERR;
        }
        Date now = new Date();
        // 在开始之前或结束之后则直接返回错误码
        if (now.compareTo(commodityDetailVo.getStartAt()) < 0 ){
            return CommodityOrderResult.TIME_LIMIT_NOT_ARRIVED;
        }
        if (now.compareTo(commodityDetailVo.getEndAt()) > 0){
            return CommodityOrderResult.TIME_LIMIT_EXCEED;
        }
        // 预减库存限流
        if(!commodityClient.decrementStock(commodityOrderVo.getCommodityId())){
            return CommodityOrderResult.LIMIT_EXCEED;
        }
        if (commodityDetailVo.getPrice().compareTo(BigDecimal.ZERO) > 0){
            UserDetailVo userDetailVo = userClient.getUserDetailVoById(commodityDetailVo.getUserId());
            // 收费商品判断金币是否充足
            userDetailVo.setCoin(userDetailVo.getCoin().subtract(commodityDetailVo.getPrice()));
            if (userDetailVo.getCoin().compareTo(BigDecimal.ZERO) < 0){
                // 金币不足，不合法，请求能发送过来金币一定是够的
                return Result.REQUEST_ERR;
            }
        }
        // 创建订单
        Integer execute = transactionTemplate.execute(transactionStatus -> {
            try {
                CommodityOrder commodityOrder = new CommodityOrder();
                commodityOrder.setUserId(commodityOrderVo.getUserId());
                commodityOrder.setCommodityId(commodityOrderVo.getCommodityId());
                commodityOrder.setCreatedAt(new Date());
                commodityOrder.setStatus((byte) 1);
                commodityOrder.setPrice(commodityDetailVo.getPrice());
                commodityOrderMapper.insert(commodityOrder);
                // 构建消息
                CommodityOrderMessage commodityOrderMessage = new CommodityOrderMessage(
                        commodityOrder.getId(),
                        commodityOrder.getCommodityId(),
                        1,
                        commodityOrder.getUserId(),
                        commodityOrder.getPrice(),
                        commodityOrder.getCreatedAt(),
                        commodityOrder.getStatus()
                );
                // 发送消息到队列
                mqSenderManager.sendToStockDecrementQueue(commodityOrderMessage);
            } catch (DuplicateKeyException e) {
                log.warn("订单重复创建！" + e.toString());
                transactionStatus.setRollbackOnly();
                return -1;
            } catch (Exception e) {
                log.error("创建订单过程发生错误！" + e.toString());
                transactionStatus.setRollbackOnly();
                return -2;
            }
            return 0;
        });
        // 下单失败，回滚两个缓存，但重复订单不会删除防重复下单的缓存
        if (execute == null){
            cacheManager.removeCommodityOrderVo(commodityOrderVo);
            commodityClient.incrementStock(commodityOrderVo.getCommodityId());
            return Result.SERVER_ERROR;
        }else if (execute == 0){
            return Result.SUCCESS;
        }else if (execute == -1){
            // 重复订单将不会删除防止重复下单的缓存
            commodityClient.incrementStock(commodityOrderVo.getCommodityId());
            return CommodityOrderResult.REPEATED_REQUEST;
        }else{
            cacheManager.removeCommodityOrderVo(commodityOrderVo);
            commodityClient.incrementStock(commodityOrderVo.getCommodityId());
            return Result.SERVER_ERROR;
        }
    }

    @Override
    public Result check(Long userId, Long spikeId) {
        return null;
    }

    @Override
    public Result delete(Long userId, Long id, Long spikeId) {
        return null;
    }
}
