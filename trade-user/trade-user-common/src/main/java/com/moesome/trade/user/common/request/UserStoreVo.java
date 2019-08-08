package com.moesome.trade.user.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStoreVo {
	@NotNull
	@Size(min = 1,max = 60)
	private String username;

	@NotNull
	@Size(min = 1,max = 60)
	private String nickname;

	private String password;

	@NotNull
	@Size(min = 1,max = 64)
	private String email;

	@NotNull
	@Size(min = 11,max = 11)
	private String phone;
}
