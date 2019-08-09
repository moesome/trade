package com.moesome.trade.user.common.response.result;


import com.moesome.trade.common.code.Code;
import com.moesome.trade.common.code.ErrorCode;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.user.common.response.vo.UserDetailVo;

public class UserResult extends Result<UserDetailVo> {
	// 用于登录错误
	public static final UserResult USERNAME_OR_PASSWORD_ERR = new UserResult(ErrorCode.USERNAME_OR_PASSWORD_ERR);

	// 用于传入的用户没有权限操作

	public static final UserResult USER_DUPLICATE = new UserResult(ErrorCode.USER_DUPLICATE);
	public UserResult(Code code) {
		super(code);
	}

	public UserResult(Code code, UserDetailVo object) {
		super(code, object);
	}
}
