package com.findplan.auth;

public enum TokenType {
	ACCESS(1000L * 60 * 30), REFRESH(1000L * 60 * 60 * 24 * 7);
	
	private final long expireTime;
	
	TokenType(long expireTime) {
		this.expireTime = expireTime;
	}
	
	public long getExpireTime() {
		return expireTime;
	}
	
}
