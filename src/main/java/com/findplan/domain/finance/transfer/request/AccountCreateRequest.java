package com.findplan.domain.finance.transfer.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountCreateRequest {
	
	// 계좌명
	private final String accountName;
	
	// 초기 잔액
	private final Long initialBalance;
	
}
