package com.moesome.trade.recharge.server.service;

import com.moesome.trade.common.response.Result;
import com.moesome.trade.recharge.server.manager.AliPayManager;
import com.moesome.trade.recharge.server.model.dao.RechargeMapper;
import com.moesome.trade.recharge.server.model.domain.Recharge;
import com.moesome.trade.recharge.server.model.vo.AliPayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

@Service
@Slf4j
public class RechargeServiceImpl implements RechargeService {
    @Autowired
    private AliPayManager aliPayManager;

    @Autowired
    private RechargeMapper rechargeMapper;

    @Override
    public Result recharge(HttpServletResponse httpServletResponse, Long userId, BigDecimal coin) {
        // 创建订单
        Recharge recharge = new Recharge();
        recharge.setCreatedAt(new Date());
        recharge.setStatus((byte)0);
        // 暂时只有支付宝
        recharge.setWay((byte)1);
        recharge.setCoin(coin);
        recharge.setUserId(userId);
        rechargeMapper.insertSelective(recharge);

        AliPayVo aliPayVo = new AliPayVo();
        aliPayVo.setOutTradeNo(recharge.getId().toString());
        aliPayVo.setSubject("用户充值");
        aliPayVo.setTotalAmount(coin.floatValue());

        aliPayManager.pay(httpServletResponse,"recharge",aliPayVo);
        // 在 pay 里页面被重定向到支付页面
        return null;
    }

    @Override
    public boolean check(String out_trade_no) {
        AliPayVo aliPayVo = new AliPayVo();
        aliPayVo.setOutTradeNo(out_trade_no);
        return aliPayManager.check(aliPayVo);
    }

    @Override
    public void resolve(String id, String amount, String payAt, String trade_no) {
        aliPayManager.resolve(id,amount,payAt,trade_no);
    }

    @Scheduled(initialDelay=30*1000,fixedRate = 3*60*1000)
    public void scanAndResolveRecharge(){
        log.debug("开始执行定时任务");
        Date now = new Date(System.currentTimeMillis() - 40*60*1000);
        List<Recharge> recharges = rechargeMapper.selectAllUnResolver();
        recharges.stream()
                .filter(
                        // 将所有的订单 check 一次，若 check 失败了 and 订单创建时间到现在已经超过 40 分钟
                        // 收集这些订单进行下一步处理
                        ((Predicate<Recharge>) recharge -> !check(recharge.getId().toString()))
                                .and(recharge -> recharge.getCreatedAt().before(now))
                )
                // 遍历过期且未支付订单，对其进行标记
                .forEach(recharge -> {
                    log.debug("发现过期订单: "+recharge);
                    recharge.setStatus((byte)-1);
                    rechargeMapper.updateByPrimaryKeySelective(recharge);
                });
    }
}
