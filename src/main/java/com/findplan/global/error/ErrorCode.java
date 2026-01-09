package com.findplan.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	USER_NOT_FOUND(404, "U01", "사용자를 찾을 수 없습니다."),
	EMAIL_DUPLICATION(400, "U02", "이미 사용중인 이메일 입니다."),
	NICKNAME_DUPLICATION(400, "U03", "이미 사용중인 닉네임 입니다."),
	LOGIN_NOT_MATCHES(401, "U04", "아이디와 비밀번호가 일치하지 않습니다."),
	PASSWORD_SAME_UPDATE(401, "U05", "기존 비밀번호와 동일하게 변경할 수 없습니다."),
	NOT_AUTHORIZATION(401, "U06", "사용자 권한 정보가 적합하지 않습니다."),
	
	// 심각한 인증 오류
	IDENTITY_NOT_FOUND(401, "U07", "인증 정보가 유효하지 않습니다."),
	
	PASSWORD_NOT_MATCH(401, "U08", "비밀번호가 일치하지 않습니다.");
	
	private final int status;
	
	private final String code;
	
	private final String message;
	
}