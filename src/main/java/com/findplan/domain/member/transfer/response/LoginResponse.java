package com.findplan.domain.member.transfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class LoginResponse {

	private String accessToken;
	
	private String email;
	
	private String nickname;
}
