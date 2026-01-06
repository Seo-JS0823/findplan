package com.findplan.global.error;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final ErrorCode errorCode;
	
	public GlobalException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
	
}
