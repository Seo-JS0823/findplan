package com.findplan.transfer.response;

public enum ErrorMessage {
	DUPLI_NICKNAME("이미 사용중인 닉네임 입니다."),
	DUPLI_EMAIL("이미 사용중인 이메일 입니다."),
	NOT_FOUND_MEMBER("존재하지 않는 유저입니다.");
	
	private String message;
	
	ErrorMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
}
