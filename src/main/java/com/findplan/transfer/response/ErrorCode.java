package com.findplan.transfer.response;

import java.util.Map;

public enum ErrorCode {
	DUPLI_EMAIL("MA-01", "이미 사용중인 이메일 입니다."),
	DUPLI_NICKNAME("MA-02", "이미 사용중인 닉네임 입니다."),
	NOT_FOUND_EMAIL("MA-03", "존재하지 않는 이메일 입니다."),
	PASSWORD_NOT_MATCH("MA-04", "비밀번호가 일치하지 않습니다");
	
	private String errorCode;
	
	private String message;
	
	ErrorCode(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}
	
	public Map<String, String> getResponse() {
		return new ErrorCodeResponse(errorCode, message).getResponse();
	}
	
}

