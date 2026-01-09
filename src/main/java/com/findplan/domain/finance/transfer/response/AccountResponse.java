package com.findplan.domain.finance.transfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AccountResponse {
	
	// 계좌명
	private final String accountName;
	
	// 현재 잔액
	private final Long currentBalance;
	
}
