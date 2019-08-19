package com.moesome.trade.order.server.model.domain;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageCommodityOrder {
    private Long orderId;

    private Long commodityId;

    private Integer stockDecrement;

    private Date stockDecrementAt;

    private Long userId;

    private BigDecimal coinDecrement;

    private Date coinDecrementAt;

    private Byte status;

    private Date createdAt;
}