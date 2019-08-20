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
     * -3：扣金币失败<br />
     * -2：扣库存失败<br />
     * 0：成功<br />
     * 1：订单创建<br />
     * 10：用户催单<br />
     * 20：商家发货<br />
     * 30：订单完成<br />
    */
    private Byte status;
}