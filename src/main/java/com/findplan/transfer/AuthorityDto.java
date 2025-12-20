package com.findplan.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthorityDto {

	private String deviceInfo;
	
	private String ip;
	
	private String deviceId;
	
	private String refreshToken;
	
	public AuthorityDto() {}
}
