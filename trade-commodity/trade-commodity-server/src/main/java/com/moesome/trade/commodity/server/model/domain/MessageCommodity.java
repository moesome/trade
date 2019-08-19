package com.moesome.trade.commodity.server.model.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageCommodity {
    private Long orderId;

    private Long commodityId;

    private Integer stockDecrement;

    private Date stockDecrementAt;

    private Date createdAt;
}