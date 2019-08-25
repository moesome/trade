package com.moesome.trade.recharge.server.controller;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.recharge.server.config.AliPayConfig;
import com.moesome.trade.recharge.server.service.RechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("recharge")
@Slf4j
public class RechargeController {
	@Autowired
	private RechargeService rechargeService;

	@GetMapping("{coin}")
	public Result recharge(HttpServletResponse httpServletResponse,@RequestHeader("user-id") Long userId, @PathVariable BigDecimal coin){
		if (coin.compareTo(BigDecimal.ZERO) <= 0)
			return Result.REQUEST_ERR;
		return rechargeService.recharge(httpServletResponse,userId,coin);
	}

	// TODO 定时任务
	/**
	 * 接用户回调，该回调即使不执行也没什么问题，定时任务会处理订单
	 * @param msg
	 * @param httpServletResponse
	 */
	@GetMapping("return")
	public void returnURL(@RequestParam Map<String, String> msg, HttpServletResponse httpServletResponse){
		boolean signVerified = false; //调用SDK验证签名
		try {
			signVerified = AlipaySignature.rsaCheckV1(msg, AliPayConfig.ALIPAY_PUBLIC_KEY, AliPayConfig.CHARSET, AliPayConfig.SIGN_TYPE);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		log.debug("Return:"+msg);
		if(signVerified){
			// 验签
			String appId = msg.get("auth_app_id");
			if (appId.equals(AliPayConfig.APP_ID)){
				rechargeService.check(msg.get("out_trade_no"));
			}else{
				log.debug("error");
			}
		}else{
			log.debug("验签失败");
		}
		try {
			httpServletResponse.sendRedirect(httpServletResponse.encodeRedirectURL("https://trade.moesome.com"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接支付宝回调，该回调即使不执行也没什么问题，定时任务会处理订单
	 * @param msg
	 */
	@PostMapping("notify")
	public void notifyURL(@RequestParam Map<String, String> msg){
		log.debug("收到 notify");
		boolean signVerified = false; //调用SDK验证签名
		try {
			signVerified = AlipaySignature.rsaCheckV1(msg, AliPayConfig.ALIPAY_PUBLIC_KEY, AliPayConfig.CHARSET, AliPayConfig.SIGN_TYPE);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		if(signVerified){
			// 状态 TRADE_SUCCESS 成功过
			String status = msg.get("trade_status");
			// 验证
			String appId = msg.get("auth_app_id");
			if (status.equals("TRADE_SUCCESS")&&appId.equals(AliPayConfig.APP_ID)){
				log.debug("进入 resolver");
				String id = msg.get("out_trade_no");
				String payAt = msg.get("gmt_payment");
				String trade_no = msg.get("trade_no");
				String amount = msg.get("total_amount");
				rechargeService.resolve(id,amount,payAt,trade_no);
			}else{
				log.debug("error");
			}
		}else{
			log.debug("验签失败");
		}
		log.debug("notify"+msg);
	}
}
