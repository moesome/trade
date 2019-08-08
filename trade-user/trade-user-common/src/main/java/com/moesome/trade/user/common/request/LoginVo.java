package com.moesome.trade.user.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {
	@NotNull
	private String username;

	@NotNull
	@Length(min = 32,max = 32)
	private String password;
}
