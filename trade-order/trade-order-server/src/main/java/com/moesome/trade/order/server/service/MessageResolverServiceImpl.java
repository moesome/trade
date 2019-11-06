package com.moesome.trade.order.server.service;

import com.moesome.trade.commodity.client.CommodityClient;
import com.moesome.trade.common.message.CommodityOrderMessage;
import com.moesome.trade.order.common.request.CommodityOrderVo;
import com.moesome.trade.order.server.manager.CacheManager;
import com.moesome.trade.order.server.manager.MqSenderManager;
import com.moesome.trade.order.server.manager.RedisCacheManager;
import com.moesome.trade.order.server.model.dao.CommodityOrderMapper;
import com.moesome.trade.order.server.model.dao.MessageCommodityOrderMapper;
import com.moesome.trade.order.server.model.domain.CommodityOrder;
import com.moesome.trade.order.server.model.domain.MessageCommodityOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class MessageResolverServiceImpl implements MessageResolverService{
    @Autowired
    private CommodityOrderMapper commodityOrderMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MessageCommodityOrderMapper messageCommodityOrderMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CommodityClient commodityClient;

    @Autowired
    private MqSenderManager mqSenderManager;

    @Override
    public void resolveFailed(CommodityOrderMessage commodityOrderMessage) {
        Long orderId = commodityOrderMessage.getOrderId();
        MessageCommodityOrder messageCommodityOrder = new MessageCommodityOrder(
                orderId,
                commodityOrderMessage.getCommodityId(),
                commodityOrderMessage.getStockDecrement(),
                commodityOrderMessage.getStockDecrementAt(),
                commodityOrderMessage.getUserId(),
                commodityOrderMessage.getCoinDecrement(),
                commodityOrderMessage.getCoinDecrementAt(),
                commodityOrderMessage.getStatus(),
                commodityOrderMessage.getCreatedAt()
        );
        try{
            transactionTemplate.execute(new TransactionCallback<Integer>() {
                @Override
                public Integer doInTransaction(TransactionStatus status) {
                    messageCommodityOrderMapper.insert(messageCommodityOrder);
                    log.debug("处理失败状态为: "+commodityOrderMessage.getStatus()+" 的订单");
                    log.debug("在缓存中设置 orderId:"+orderId+"状态为 FAILED");
                    cacheManager.saveCommodityOrderResult(commodityOrderMessage.getUserId(),commodityOrderMessage.getCommodityId(), RedisCacheManager.FAILED);
                    CommodityOrderVo commodityOrderVo = new CommodityOrderVo();
                    commodityOrderVo.setUserId(commodityOrderMessage.getUserId());
                    commodityOrderVo.setCommodityId(commodityOrderMessage.getCommodityId());
                    // 订单处理时出错，回滚订单创建缓存和预减库存
                    log.debug("删除 orderId:"+orderId+"订单");
                    commodityOrderMapper.deleteByPrimaryKey(orderId);
                    log.debug("从缓存中删除限制下单信息: "+commodityOrderVo);
                    cacheManager.removeCommodityOrderVo(commodityOrderVo);
                    if (commodityOrderMessage.getStatus() == -3){
                        // 金币不足，说明金币的缓存可能存在问题

                        // 需要额外回滚数据库库存
                        mqSenderManager.sendToStockRollbackQueue(commodityOrderMessage);
                    }else{
                        // 库存不足，说明库存的缓存可能存在问题

                        log.debug("回滚缓存的库存");
                        commodityClient.incrementStock(commodityOrderMessage.getCommodityId());
                    }
                    return null;
                }
            });
        }catch (DuplicateKeyException e){
            // 消息重复处理
            log.warn("跳过重复消息 commodityOrderMessage: "+commodityOrderMessage);
        }catch (Exception e){
            // 发生其他异常
            log.error("发生错误"+e.toString());
            throw e;// 抛出该异常，并拒绝消费该条消息
        }
    }

    @Override
    public void resolveSucceed(CommodityOrderMessage commodityOrderMessage) {
        Long orderId = commodityOrderMessage.getOrderId();
        MessageCommodityOrder messageCommodityOrder = new MessageCommodityOrder(
                orderId,
                commodityOrderMessage.getCommodityId(),
                commodityOrderMessage.getStockDecrement(),
                commodityOrderMessage.getStockDecrementAt(),
                commodityOrderMessage.getUserId(),
                commodityOrderMessage.getCoinDecrement(),
                commodityOrderMessage.getCoinDecrementAt(),
                commodityOrderMessage.getStatus(),
                commodityOrderMessage.getCreatedAt()
        );
        try{
            transactionTemplate.execute(new TransactionCallback<Integer>() {
                @Override
                public Integer doInTransaction(TransactionStatus status) {
                    messageCommodityOrderMapper.insert(messageCommodityOrder);
                    CommodityOrder commodityOrder = new CommodityOrder();
                    commodityOrder.setId(orderId);
                    commodityOrder.setStatus((byte)0);
                    commodityOrderMapper.updateByPrimaryKeySelective(commodityOrder);
                    log.debug("在缓存中设置 orderId:"+orderId+"状态为 SUCCESS");
                    cacheManager.saveCommodityOrderResult(commodityOrderMessage.getUserId(),commodityOrderMessage.getCommodityId(), RedisCacheManager.SUCCESS);
                    return null;
                }
            });
        }catch (DuplicateKeyException e){
            // 消息重复处理
            log.warn("跳过重复消息 commodityOrderMessage: "+commodityOrderMessage);
        }catch (Exception e){
            // 发生其他异常
            log.error("发生错误"+e.toString());
            throw e;// 抛出该异常，并拒绝消费该条消息
        }
    }
}
