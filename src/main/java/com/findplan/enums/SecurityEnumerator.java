package com.findplan.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityEnumerator {

	EXPIRED_TOKEN("EXPIRED_TOKEN"),
	UNAUTHO("인증 정보가 없습니다.");
	
	private final String exception;
}
