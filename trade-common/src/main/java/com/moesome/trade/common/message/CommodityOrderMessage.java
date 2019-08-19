package com.moesome.trade.common.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单号，商品号，用户名，处理时间，订单状态
 */
@Data
@NoArgsConstructor
public class CommodityOrderMessage {
    // 订单序列号
    private Long orderId;
    // 商品序列号
    private Long commodityId;
    // 商品应扣库存
    private Integer stockDecrement;
    // 扣除库存时间
    private Date stockDecrementAt;
    // 用户 id
    private Long userId;
    // 用户应扣金币
    private BigDecimal coinDecrement;
    // 扣除金币时间
    private Date coinDecrementAt;
    // 订单创建
    private Date createdAt;
    // 订单状态
    private Byte status;

    public CommodityOrderMessage(Long orderId, Long commodityId, Integer stockDecrement, Long userId, BigDecimal coinDecrement, Date createdAt, Byte status) {
        this.orderId = orderId;
        this.commodityId = commodityId;
        this.stockDecrement = stockDecrement;
        this.userId = userId;
        this.coinDecrement = coinDecrement;
        this.createdAt = createdAt;
        this.status = status;
    }
}
