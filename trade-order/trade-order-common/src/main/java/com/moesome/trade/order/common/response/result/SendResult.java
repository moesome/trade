package com.moesome.trade.order.common.response.result;

import com.moesome.trade.common.code.Code;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.order.common.response.vo.CommodityOrderDetailAndCommodityDetailVo;
import com.moesome.trade.order.common.response.vo.SendVo;
import lombok.Data;

import java.util.List;

@Data
public class SendResult extends Result<List<SendVo>> {
    Integer count;

    public SendResult(Code code) {
        super(code);
    }

    public SendResult(Code code, List<SendVo> object, Integer count) {
        super(code, object);
        this.count = count;
    }
}
