package com.moesome.trade.commodity.common.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用来接收存储/修改商品信息<br/>
 * 转化为 Commodity 实体时以下值需要服务端填充：<br/>
 * 1. id<br/>
 * 2. userId<br/>
 * 3. createdAt<br/>
 * 4. updatedAt<br/>
 */
@Data
public class CommodityStoreVo {
    @NotNull
    @Size(max = 32,min = 1)
    private String name;

    @NotNull
    private String detail;

    /**
     * 起始时间
     */
    @NotNull
    private Date startAt;

    /**
     * 结束时间
     */
    @NotNull
    private Date endAt;

    @Min(0)
    private Integer stock;

    @NotNull
    @Min(0)
    @Max(1000000)
    private BigDecimal price;
}
