package com.moesome.trade.commodity.common.response.result;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.common.code.Code;
import com.moesome.trade.common.code.ErrorCode;
import com.moesome.trade.common.response.Result;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommodityResult extends Result<List<CommodityDetailVo>> {
    Integer count;

    Date now;

    public static final CommodityResult CANT_MODIFY_AFTER_START = new CommodityResult(ErrorCode.CANT_MODIFY_AFTER_START);

    public static final CommodityResult START_TIME_NOT_ALLOWED = new CommodityResult(ErrorCode.START_TIME_NOT_ALLOWED);

    public static final CommodityResult LIMIT_EXCEED = new CommodityResult(ErrorCode.LIMIT_EXCEED);

    public CommodityResult(Code code) {
        super(code);
    }

    public CommodityResult(Code code, List<CommodityDetailVo> commodityDetailVoList, Integer count) {
        super(code, commodityDetailVoList);
        this.count = count;
        this.now = new Date();
    }
}
