package com.moesome.trade.order.common.response.result;

import com.moesome.trade.common.code.Code;
import com.moesome.trade.common.code.ErrorCode;
import com.moesome.trade.common.code.SuccessCode;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.order.common.response.vo.CommodityOrderDetailAndCommodityDetailVo;
import lombok.Data;

import java.util.List;

@Data
public class CommodityOrderResult extends Result<List<CommodityOrderDetailAndCommodityDetailVo>> {
    Integer count;

    public static CommodityOrderResult REPEATED_REQUEST = new CommodityOrderResult(ErrorCode.REPEATED_REQUEST);
    public static CommodityOrderResult TIME_LIMIT_NOT_ARRIVED = new CommodityOrderResult(ErrorCode.TIME_LIMIT_NOT_ARRIVED);
    public static CommodityOrderResult TIME_LIMIT_EXCEED = new CommodityOrderResult(ErrorCode.TIME_LIMIT_EXCEED);
    public static CommodityOrderResult LIMIT_EXCEED = new CommodityOrderResult(ErrorCode.LIMIT_EXCEED);
    public static CommodityOrderResult RESOLVING = new CommodityOrderResult(SuccessCode.RESOLVING);
    public static CommodityOrderResult FAILED = new CommodityOrderResult(ErrorCode.FAILED);

    public CommodityOrderResult(Code code) {
        super(code);
    }

    public CommodityOrderResult(Code code, List<CommodityOrderDetailAndCommodityDetailVo> object, Integer count) {
        super(code, object);
        this.count = count;
    }
}
