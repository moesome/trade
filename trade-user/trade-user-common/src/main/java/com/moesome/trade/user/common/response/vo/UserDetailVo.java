package com.moesome.trade.user.common.response.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 可传递的用户信息<br />
 * 由 store 转化为 detailVo 进行返回时，需要填充 coin 信息<br />
 */
@Data
public class UserDetailVo {
    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private BigDecimal coin;
}
