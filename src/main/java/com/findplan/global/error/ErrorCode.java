package com.findplan.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	USER_NOT_FOUND(404, "U01", "사용자를 찾을 수 없습니다."),
	EMAIL_DUPLICATION(400, "U02", "이미 사용중인 이메일 입니다."),
	NICKNAME_DUPLICATION(400, "U03", "이미 사용중인 닉네임 입니다."),
	LOGIN_NOT_MATCHES(401, "U04", "아이디와 비밀번호가 일치하지 않습니다.");
	
	private final int status;
	
	private final String code;
	
	private final String message;
	
}