package com.moesome.trade.recharge.server.service;

import com.moesome.trade.common.response.Result;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

public interface RechargeService {
    Result recharge(HttpServletResponse httpServletResponse, Long userId, BigDecimal coin);

    /**
     * 向支付宝发起校验请求，校验成功则处理订单
     * @param out_trade_no
     * @return
     */
    boolean check(String out_trade_no);

    void resolve(String id, String amount, String payAt, String trade_no);
}
