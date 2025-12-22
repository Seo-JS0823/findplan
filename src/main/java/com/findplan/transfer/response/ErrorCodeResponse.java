package com.findplan.transfer.response;

import java.util.Map;

public class ErrorCodeResponse {

	private String errorCode;
	
	private String message;
	
	public ErrorCodeResponse(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}
	
	public Map<String, String> getResponse() {
		return Map.of("errorCode", errorCode, "errorMessage", message);
	}
}
