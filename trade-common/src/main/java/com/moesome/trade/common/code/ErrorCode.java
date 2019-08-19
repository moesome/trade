package com.moesome.trade.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode implements Code{
	// 请求相关错误
	REQUEST_ERR(-1000,"请求错误"),
	CANT_MODIFY_AFTER_START(-1001,"秒杀开始后无法修改商品"),
	START_TIME_NOT_ALLOWED(-1002,"开始时间必须在结束时间之前"),
	REPEATED_REQUEST(-1003,"重复的请求"),
	TIME_LIMIT_EXCEED(-1004,"超过结束时间"),
	TIME_LIMIT_NOT_ARRIVED(-1005,"未到达开始时间"),
	LIMIT_EXCEED(-1006,"超出数量限制"),


	// 认证相关错误
	AUTHORIZED_ERR(-2000,"认证异常，请重新登录。"),

	// 用户相关错误
	USER_DUPLICATE(-3000,"用户名重复"),

	// 客户端异常
	CLIENT_ERROR(-4000,"客户端异常"),

	// 服务器异常
	SERVER_ERROR(-5000,"服务器异常"),

	UNAUTHORIZED(-401,"未认证"),
	UNKNOWN_ERR(-402,"未知错误"),
	USERNAME_OR_PASSWORD_ERR(-629,"用户名或密码错误"),


	IN_QUEUE(-512,"请求在队列中"),
	FAILED(-513,"请求失败"),
	;

	private int code;
	private String message;
}
