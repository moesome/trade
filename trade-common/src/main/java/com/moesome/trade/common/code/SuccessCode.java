package com.moesome.trade.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  SuccessCode implements Code{
	OK(0,"成功"),
	RESOLVING(1000,"处理中")
	;

	private int code;
	private String message;

}
