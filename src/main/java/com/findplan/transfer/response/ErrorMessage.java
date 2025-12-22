package com.findplan.transfer.response;

public enum ErrorMessage {
	NOT_FOUND_MEMBER("존재하지 않는 유저입니다.");
	
	private String message;
	
	ErrorMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
}
