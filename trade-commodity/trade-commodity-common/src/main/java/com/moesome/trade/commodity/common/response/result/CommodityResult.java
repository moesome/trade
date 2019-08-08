package com.moesome.trade.commodity.common.response.result;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.common.code.Code;
import com.moesome.trade.common.response.Result;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommodityResult extends Result<List<CommodityDetailVo>> {
    Integer count;

    Date now;

    public CommodityResult(Code code) {
        super(code);
    }

    public CommodityResult(Code code, List<CommodityDetailVo> commodityDetailVoList, Integer count) {
        super(code, commodityDetailVoList);
        this.count = count;
        this.now = new Date();
    }
}
