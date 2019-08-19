package com.moesome.trade.order.common.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommodityOrderVo {
    private Long userId;
    @NotNull
    private Long commodityId;
//    // 用于在订单处理失败回滚时候给缓存中用户的金币回滚
//    private String sessionId;
}
