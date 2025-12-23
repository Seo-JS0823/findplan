package com.findplan.service.exception;

public class RefreshTokenAndEmailNotMatchException extends RuntimeException {
	
	public RefreshTokenAndEmailNotMatchException() {
		
	}
	
	public RefreshTokenAndEmailNotMatchException(String message) {
		super(message);
	}
	
}
