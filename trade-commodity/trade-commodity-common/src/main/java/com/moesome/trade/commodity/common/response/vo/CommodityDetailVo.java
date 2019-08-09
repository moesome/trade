package com.moesome.trade.commodity.common.response.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 由 storeVo 转化为 detailVo 需要填充以下数据:<br />
 * 1. id<br />
 * 2. createdAt<br />
 * 3. updatedAt<br />
 */
@Data
public class CommodityDetailVo {

    private Long id;

    private String name;

    /**
     * 创建者
     */
    private Long userId;

    private String detail;

    /**
     * 起始时间
     */
    private Date startAt;

    /**
     * 结束时间
     */
    private Date endAt;

    private Integer stock;

    private Date createdAt;

    private Date updatedAt;

    private BigDecimal price;
}
