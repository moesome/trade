package com.moesome.trade.order.common.response.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CommodityOrderDetailAndCommodityDetailVo {
    // order 相关信息
    private Long commodityOrderId;
    private Date commodityOrderCreatedAt;
    private Byte commodityOrderStatus;
    // commodity 相关信息
    private Long commodityId;
    private String commodityName;
    private Long commodityUserId;
    private String commodityDetail;
}
