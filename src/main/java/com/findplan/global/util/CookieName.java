package com.findplan.global.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookieName {

	// 디바이스 아이디
	DEVICE("DEV-FIND", 60 * 60 * 24 * 365, false),
	// 리프레시 토큰
	REFRESH("AUTH-R", 60 * 60 * 24 * 7, true),
	// 리프레시 토큰 무효화
	CLEAR_REFRESH("AUTH-R", 0, true),
	// 디바이스 아이디 무효화
	CLEAR_DEVICE("DEV-FIND", 0, true),
	// 짧은 시간 디바이스 아이디, 리프레시 토큰 검증 패스
	DEVICE_REFRESH_PASS("U-DRP", 60 * 5, true),
	// 검증 패스 무효화
	CLEAR_DEVICE_REFRESH_PASS("U-DRP", 0, true),
	// 자동 로그인 토큰
	REMEMBER_ME_TOKEN("U-RMT", 60 * 60 * 24 * 7, true),
	// 자동 로그인 토큰 무효화
	CLEAR_REMEMBER_ME_TOKEN("U-RMT", 0, true),
	
	// 로그인 완료된 페이지 띄울 때 쓸 ACCESS_TOKEN
	ACCESS("AUTH-A", 60 * 20, true),
	CLEAR_ACCESS("AUTH-A", 0, true)
	;
	
	private final String cookieName;
	
	private final int maxAge;
	
	private final boolean httpOnly;
}