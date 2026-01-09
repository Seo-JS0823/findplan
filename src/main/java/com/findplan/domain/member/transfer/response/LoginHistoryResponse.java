package com.findplan.domain.member.transfer.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginHistoryResponse {

	private String ip;
	
	private String os;
	
	private String device;
	
	private String browser;
	
	private LocalDateTime loginTime;
}
