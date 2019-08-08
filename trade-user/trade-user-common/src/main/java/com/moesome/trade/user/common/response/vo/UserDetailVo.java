package com.moesome.trade.user.common.response.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 可传递的用户信息
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
