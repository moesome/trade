package com.moesome.trade.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode implements Code{
	// 请求相关错误
	REQUEST_ERR(-1000,"请求错误"),

	// 认证相关错误
	AUTHORIZED_ERR(-2000,"认证异常，请重新登录。"),

	// 用户相关错误
	USER_DUPLICATE(-3000,"用户名重复"),



	UNAUTHORIZED(-401,"未认证"),
	UNKNOWN_ERR(-402,"未知错误"),
	USERNAME_OR_PASSWORD_ERR(-629,"用户名或密码错误"),
	REPEATED_REQUEST(-508,"重复的请求"),
	LIMIT_EXCEED(-509,"超出数量限制"),
	TIME_LIMIT_EXCEED(-510,"超过结束时间"),
	TIME_LIMIT_NOT_ARRIVED(-511,"未到达开始时间"),
	IN_QUEUE(-512,"请求在队列中"),
	FAILED(-513,"请求失败"),
	TIME_NOT_ALLOW(-514,"未在指定时间内"),
	;

	private int code;
	private String message;
}