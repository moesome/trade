package com.moesome.trade.user.server.service;

import com.moesome.trade.common.manager.DistributedLock;
import com.moesome.trade.common.message.CommodityOrderMessage;
import com.moesome.trade.user.server.manager.CacheManager;
import com.moesome.trade.user.server.manager.MqSenderManager;
import com.moesome.trade.user.server.model.dao.MessageUserMapper;
import com.moesome.trade.user.server.model.dao.UserMapper;
import com.moesome.trade.user.server.model.domain.MessageUser;
import com.moesome.trade.user.server.model.domain.User;
import com.moesome.trade.user.server.util.Transform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class MessageResolverServiceImpl implements MessageResolverService{
    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MessageUserMapper messageUserMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MqSenderManager mqSenderManager;

    @Override
    public void decrement(CommodityOrderMessage commodityOrderMessage) {
        Long userId = commodityOrderMessage.getUserId();
        BigDecimal coinDecrement = commodityOrderMessage.getCoinDecrement();
        Date now = new Date();
        MessageUser messageUser = new MessageUser(
                commodityOrderMessage.getOrderId(),
                userId,
                coinDecrement,
                now,
                commodityOrderMessage.getCreatedAt());
        try {
            distributedLock.lock(userId);
            Integer execute = transactionTemplate.execute(transactionStatus -> {
                // 信息表中检测是否已经处理，未处理才能插入，已处理则直接抛出异常并跳过该条消息
                messageUserMapper.insert(messageUser);
                // 查出商品信息并打印日志
                User user = userMapper.selectByPrimaryKey(userId);
                log.debug("用户减少金币前: user" + user);
                BigDecimal coin = user.getCoin().subtract(commodityOrderMessage.getCoinDecrement());
                if (coin.compareTo(BigDecimal.ZERO) < 0) {
                    // 回滚
                    transactionStatus.setRollbackOnly();
                    // 金币不足
                    return -1;
                }
                User userForDecrementStock = new User();
                userForDecrementStock.setId(userId);
                userForDecrementStock.setCoin(coin);
                // 减金币
                userMapper.updateByPrimaryKeySelective(userForDecrementStock);
                // 刷缓存
                user.setCoin(coin);
                cacheManager.saveUserDetailVo(Transform.transformUserToUserDetailVo(user));
                // 写入该流程处理过的信息
                commodityOrderMessage.setCoinDecrementAt(now);
                // 状态 3 已扣金币
                commodityOrderMessage.setStatus((byte)3);
                // 发送消息到队列
                mqSenderManager.sendToOrderSucceedQueue(commodityOrderMessage);
                return 0;
            });
            if (execute != null && execute == -1){
                // 减库存失败，发送订单失败消息
                // 状态 -3 在扣金币阶段发生异常
                commodityOrderMessage.setStatus((byte)-3);
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
            distributedLock.unlock(userId);
        }
    }
}
