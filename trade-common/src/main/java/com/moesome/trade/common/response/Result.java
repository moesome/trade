package com.moesome.trade.common.response;


import com.moesome.trade.common.code.Code;
import com.moesome.trade.common.code.ErrorCode;
import com.moesome.trade.common.code.SuccessCode;
import lombok.Data;

@Data
public class Result<T> {
	private Integer code;
	private String message;
	private Long timestamp;
	private T object;

	public static final Result SUCCESS = new Result(SuccessCode.OK);
	public static final Result AUTHORIZED_ERR = new Result(ErrorCode.AUTHORIZED_ERR);
	public static final Result REQUEST_ERR = new Result(ErrorCode.REQUEST_ERR);

	public Result(Code code) {
		this(code,null);
	}

	public Result(Code code, T object) {
		this.code = code.getCode();
		this.message = code.getMessage();
		this.timestamp = System.currentTimeMillis();
		this.object = object;
	}
}
