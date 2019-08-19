package com.moesome.trade.order.server.model.domain;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class CommodityOrder {
    private Long id;

    /**
    * 消费者 id
    */
    private Long userId;

    private BigDecimal price;

    private Long commodityId;

    private Date createdAt;

    /**
    * 1.待发货
    2.用户催单
    3.所有者已发送奖品
    4.完成订单
    5.订单异常
    */
    private Byte status;
}