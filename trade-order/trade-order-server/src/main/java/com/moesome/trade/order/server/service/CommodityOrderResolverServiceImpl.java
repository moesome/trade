package com.moesome.trade.order.server.service;

import com.moesome.trade.commodity.client.CommodityClient;
import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.common.code.SuccessCode;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.notification.client.NotificationClient;
import com.moesome.trade.notification.common.request.NotificationType;
import com.moesome.trade.notification.common.request.NotificationVo;
import com.moesome.trade.order.common.response.result.SendResult;
import com.moesome.trade.order.common.response.vo.SendVo;
import com.moesome.trade.order.server.model.dao.CommodityOrderMapper;
import com.moesome.trade.order.server.model.domain.CommodityOrder;
import com.moesome.trade.user.client.UserClient;
import com.moesome.trade.user.common.response.vo.UserDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class CommodityOrderResolverServiceImpl implements CommodityOrderResolverService{

    @Autowired
    private CommodityOrderMapper commodityOrderMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private CommodityClient commodityClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Result remindToSendProduction(Long userId, Long commodityOrderId) {
        // 查出当前订单，订单查询不频繁，没有加入缓存
        CommodityOrder commodityOrder = commodityOrderMapper.selectByPrimaryKey(commodityOrderId);
        if (commodityOrder == null){
            return Result.REQUEST_ERR;
        }
        // 校验所有权
        if (commodityOrder.getUserId().equals(userId)){
            // 根据订单找出商品
            CommodityDetailVo commodityDetailVo = commodityClient.getByCommodityId(commodityOrder.getCommodityId());
            // 获取当前用户信息（优先查的缓存）
            UserDetailVo orderOwner= userClient.getUserDetailVoById(userId);
            // 获取商品所有者信息（优先查的缓存）
            UserDetailVo commodityOwner= userClient.getUserDetailVoById(commodityDetailVo.getUserId());
            // 订单状态如果正常则进入发送流程
            if (commodityOrder.getStatus() == 0){
                // 改变订单状态
                CommodityOrder commodityOrderToChangeStatus = new CommodityOrder();
                commodityOrderToChangeStatus.setId(commodityOrderId);
                // 状态 10 已经催单
                commodityOrderToChangeStatus.setStatus((byte) 10);
                commodityOrderMapper.updateByPrimaryKeySelective(commodityOrderToChangeStatus);
                // 发送邮件通知作者发货
                NotificationVo notificationVo = new NotificationVo();
                notificationVo.setTitle("发货提醒");
                notificationVo.setMsg("用户"+orderOwner.getUsername()+"提醒您及时发送商品 id 为"+commodityDetailVo.getId()+"的“"+commodityDetailVo.getName()+"”商品");
                notificationVo.setNotificationType(NotificationType.EMAIL);
                // 发送给商品所有者
                notificationVo.setTo(commodityOwner.getEmail());
                notificationClient.notification(notificationVo);
                return Result.SUCCESS;
            }else{
                return Result.REQUEST_ERR;
            }
        }else {
            return Result.AUTHORIZED_ERR;
        }
    }

    @Override
    public Result receivedProduction(Long userId, Long commodityOrderId) {
        // 查出订单
        CommodityOrder commodityOrder = commodityOrderMapper.selectByPrimaryKey(commodityOrderId);
        if (commodityOrder == null) {
            return Result.REQUEST_ERR;
        }
        // 判断订单是否属于当前用户
        if (userId.equals(commodityOrder.getUserId())) {
            // 查出订单商品的所有者
            Long commodityId = commodityOrder.getCommodityId();
            CommodityDetailVo commodityDetailVo = commodityClient.getByCommodityId(commodityId);
            if (commodityOrder.getStatus() == 20){
                try{
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            // 更新订单状态
                            CommodityOrder commodityOrderForUpdate = new CommodityOrder();
                            commodityOrderForUpdate.setId(commodityOrder.getId());
                            commodityOrderForUpdate.setStatus((byte)30);
                            commodityOrderMapper.updateByPrimaryKeySelective(commodityOrderForUpdate);
                            // 增加商品所有者金币
                            userClient.incrementCoin(commodityOrder.getPrice(),commodityDetailVo.getUserId());
                        }
                    });
                }catch (Exception e){
                    log.error(e.toString());
                    return Result.REQUEST_ERR;
                }
                return Result.SUCCESS;
            }else{
                return Result.REQUEST_ERR;
            }
        }else{
            return Result.AUTHORIZED_ERR;
        }
    }

    @Override
    public Result sendProduction(Long userId, Long commodityOrderId) {
        CommodityOrder commodityOrder = commodityOrderMapper.selectByPrimaryKey(commodityOrderId);
        if (commodityOrder == null){
            return Result.REQUEST_ERR;
        }
        // 根据订单找出商品
        CommodityDetailVo commodityDetailVo = commodityClient.getByCommodityId(commodityOrder.getCommodityId());
        if (commodityDetailVo == null){
            return Result.REQUEST_ERR;
        }
        // 获取商品所有者信息（优先查的缓存）
        UserDetailVo commodityOwner= userClient.getUserDetailVoById(commodityDetailVo.getUserId());
        if (commodityOwner == null) {
            return Result.REQUEST_ERR;
        }
        if (userId.equals(commodityOwner.getId())) {
            // 仅在订单创建或用户催单时修改状态为已经发货
            if (commodityOrder.getStatus() == 0 || commodityOrder.getStatus() == 10) {
                CommodityOrder commodityOrderForUpdate = new CommodityOrder();
                commodityOrderForUpdate.setId(commodityOrder.getId());
                commodityOrderForUpdate.setStatus((byte)20);
                commodityOrderMapper.updateByPrimaryKeySelective(commodityOrderForUpdate);
                return Result.SUCCESS;
            }else{
                return Result.SUCCESS;
            }
        }else {
            return Result.AUTHORIZED_ERR;
        }
    }

    @Override
    public Result index(int page, String order, Long userId) {
        // 查出当前用户拥有的商品
        List<CommodityDetailVo> commodityDetailVoList = commodityClient.getByUserId(userId);
        if (commodityDetailVoList.isEmpty()){
            return Result.SUCCESS;
        }
        // 根据商品查出商品所有的订单
        List<CommodityOrder> commodityOrderList = commodityOrderMapper.selectByCommoditiesId(commodityDetailVoList);
        if (commodityOrderList.isEmpty()){
            return Result.SUCCESS;
        }
        ArrayList<Long> userIdList = new ArrayList<>();
        for (CommodityOrder commodityOrder:commodityOrderList){
            userIdList.add(commodityOrder.getUserId());
        }
        // 根据每个订单上的用户查出用户信息
        List<UserDetailVo> userDetailVoByIdList = userClient.getUserDetailVoByIdList(userIdList);
        // 组合信息
        ArrayList<SendVo> sendVos = new ArrayList<>();
        // 商品信息和订单信息为一对多，订单信息和用户信息为一对一（有序）
        // 固定商品信息，遍历所有订单信息，匹配则创建该条目加入集合
        // 遍历完成则将商品移动到下一个，开始下一轮匹配
        for (int commodityPos = 0,orderPos = 0; commodityPos < commodityDetailVoList.size();){
            CommodityDetailVo commodityDetailVo = commodityDetailVoList.get(commodityPos);
            CommodityOrder commodityOrder = commodityOrderList.get(orderPos);
            UserDetailVo userDetailVo = userDetailVoByIdList.get(orderPos);
            if (commodityDetailVo.getId().equals(commodityOrder.getCommodityId())){
                SendVo sendVo = new SendVo();
                sendVo.setCommodityOrderId(commodityOrder.getId());
                sendVo.setCreatedAt(commodityOrder.getCreatedAt());
                sendVo.setStatus(commodityOrder.getStatus());

                sendVo.setCommodityId(commodityDetailVo.getId());
                sendVo.setCommodityName(commodityDetailVo.getName());

                sendVo.setSendToUserId(userDetailVo.getId());
                sendVo.setUsername(userDetailVo.getUsername());
                sendVo.setEmail(userDetailVo.getEmail());
                sendVo.setPhone(userDetailVo.getPhone());
                sendVos.add(sendVo);
            }
            orderPos++;
            if (orderPos >= commodityOrderList.size()){
                orderPos = 0;
                commodityPos++;
            }
        }
        // 排序
        if (order.equals("ASC")){
            sendVos.sort((o1, o2) -> (int) (-o1.getCommodityOrderId() + o2.getCommodityOrderId()));
        }else{
            sendVos.sort((o1, o2) -> (int) (o1.getCommodityOrderId() - o2.getCommodityOrderId()));
        }
        List<SendVo> sendVos1 = sendVos.subList(Math.min(10 * (page - 1), sendVos.size()), Math.min(10 * (page - 1) + 10, sendVos.size()));
        return new SendResult(SuccessCode.OK,sendVos1,sendVos.size());
    }
}
