package com.moesome.trade.user.server.model.domain;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageUser {
    private Long orderId;

    private Long userId;

    private BigDecimal coinDecrement;

    private Date coinDecrementAt;

    private Date createdAt;
}