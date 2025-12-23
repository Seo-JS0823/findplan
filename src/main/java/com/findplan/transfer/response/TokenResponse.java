package com.findplan.transfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TokenResponse {

	private final String accessToken;
	
	private final String refreshToken;
	
	private final String deviceId;
}
