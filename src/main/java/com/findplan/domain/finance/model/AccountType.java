package com.findplan.domain.finance.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {

	CASH("현금"),
	BANK_ACCOUNT("은행 계좌"),
	CREDIT_CARD("신용 카드"),
	SAVING("저축");
	
	private final String accountType;
}
