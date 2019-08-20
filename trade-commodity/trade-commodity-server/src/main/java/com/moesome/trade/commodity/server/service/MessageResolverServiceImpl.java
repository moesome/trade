package com.moesome.trade.commodity.server.service;

import com.moesome.trade.commodity.common.response.result.CommodityResult;
import com.moesome.trade.commodity.server.manager.MqSenderManager;
import com.moesome.trade.commodity.server.model.dao.CommodityMapper;
import com.moesome.trade.commodity.server.model.dao.MessageCommodityMapper;
import com.moesome.trade.commodity.server.model.domain.Commodity;
import com.moesome.trade.commodity.server.model.domain.MessageCommodity;
import com.moesome.trade.common.manager.DistributedLock;
import com.moesome.trade.common.message.CommodityOrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
@Slf4j
public class MessageResolverServiceImpl implements MessageResolverService{

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private MessageCommodityMapper messageCommodityMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CommodityMapper commodityMapper;

    @Autowired
    private MqSenderManager mqSenderManager;

    @Override
    public void decrement(CommodityOrderMessage commodityOrderMessage) {
        Long commodityId = commodityOrderMessage.getCommodityId();
        Integer stockDecrement = commodityOrderMessage.getStockDecrement();
        Date now = new Date();
        MessageCommodity messageCommodity = new MessageCommodity(
                    commodityOrderMessage.getOrderId(),
                    commodityId,
                    stockDecrement,
                    now,
                    commodityOrderMessage.getCreatedAt());
        try {
            distributedLock.lock(commodityId);
            Integer execute = transactionTemplate.execute(transactionStatus -> {
                // 信息表中检测是否已经处理，未处理才能插入，已处理则直接抛出异常并跳过该条消息
                messageCommodityMapper.insert(messageCommodity);
                // 查出商品信息并打印日志
                Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);
                log.debug("商品减少库存前: commodity" + commodity);
                int stock = commodity.getStock() - stockDecrement;
                if (stock < 0) {
                    // 回滚
                    transactionStatus.setRollbackOnly();
                    // 库存不足
                    return -1;
                }
                Commodity commodityForDecrementStock = new Commodity();
                commodityForDecrementStock.setId(commodityId);
                commodityForDecrementStock.setStock(stock);
                // 减库存
                commodityMapper.updateByPrimaryKeySelective(commodityForDecrementStock);
                // 写入该流程处理过的信息
                commodityOrderMessage.setStockDecrementAt(now);
                // 状态 2 已扣库存
                commodityOrderMessage.setStatus((byte)2);
                // 发送消息到队列
                mqSenderManager.sendToCoinDecrementQueue(commodityOrderMessage);
                return 0;
            });
            if (execute != null && execute == -1){
                // 减库存失败，发送订单失败消息
                // 状态 -2 在扣库存阶段发生异常
                commodityOrderMessage.setStatus((byte)-2);
                mqSenderManager.sendToOrderFailedQueue(commodityOrderMessage);
            }
            log.debug("处理成功 commodityOrderMessage"+commodityOrderMessage);
        }catch (DuplicateKeyException e){
            // 消息重复处理
            log.warn("跳过重复消息 commodityOrderMessage: "+commodityOrderMessage);
        }catch (Exception e){
            // 发生其他异常
            log.error("发生错误"+e.toString());
            throw e;// 抛出该异常，并拒绝消费该条消息
        } finally {
            distributedLock.unlock(commodityId);
        }
    }

    @Override
    public void increment(CommodityOrderMessage commodityOrderMessage) {
        Long commodityId = commodityOrderMessage.getCommodityId();
        Integer stockDecrement = commodityOrderMessage.getStockDecrement();
        Date now = new Date();
        MessageCommodity messageCommodity = new MessageCommodity(
                commodityOrderMessage.getOrderId(),
                commodityId,
                stockDecrement,
                now,
                commodityOrderMessage.getCreatedAt());
        try {
            distributedLock.lock(commodityId);
            transactionTemplate.execute(transactionStatus -> {
                // 信息表中检测是否已经处理，未处理才能插入，已处理则直接抛出异常并跳过该条消息
                // 之前处理的消息
                MessageCommodity messageCommodityResolved = messageCommodityMapper.selectByPrimaryKey(messageCommodity.getOrderId());
                if (messageCommodityResolved.getStockDecrement() > 0){
                    // 设置消息中库存减少数为 0 表示已经回滚
                    MessageCommodity messageCommodityForUpdate = new MessageCommodity();
                    messageCommodityForUpdate.setOrderId(messageCommodity.getOrderId());
                    messageCommodityForUpdate.setStockDecrement(0);
                    messageCommodityMapper.updateByPrimaryKeySelective(messageCommodityForUpdate);
                }else{
                    // 已经被处理过了
                    log.warn("跳过重复消息 commodityOrderMessage: "+commodityOrderMessage);
                    return 0;
                }
                // 查出商品信息并打印日志
                Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);
                log.debug("商品回滚库存前: commodity" + commodity);
                int stock = commodity.getStock() + stockDecrement;
                Commodity commodityForDecrementStock = new Commodity();
                commodityForDecrementStock.setId(commodityId);
                commodityForDecrementStock.setStock(stock);
                // 加库存
                commodityMapper.updateByPrimaryKeySelective(commodityForDecrementStock);
                // 写入该流程处理过的信息
                commodityOrderMessage.setStockDecrementAt(now);
                return 0;
            });
            log.debug("处理成功 commodityOrderMessage"+commodityOrderMessage);
        }catch (Exception e){
            // 发生其他异常
            log.error("发生错误"+e.toString());
            throw e;// 抛出该异常，并拒绝消费该条消息
        } finally {
            distributedLock.unlock(commodityId);
        }
    }
}
