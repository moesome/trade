package com.moesome.trade.order.common.response.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendVo {
    // 订单信息
    private Long commodityOrderId;
    private Date createdAt;
    private Byte status;

    // 订单对应的商品信息 commodity
    private Long commodityId;
    private String commodityName;

    // 订单对用的下单用户信息
    private Long sendToUserId;
    private String username;
    private String email;
    private String phone;
}
