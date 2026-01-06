package com.findplan.global.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
	ACCESS(1000L * 60 * 30), REFRESH(1000L * 60 * 60 * 24 * 7);
	
	private final long expireTime;
}
