package com.findplan.config.security.jwt;

/*
 * 2025-12-20
 * TokenType Token 생성에 필요한 Expire Time을 지정하는 Enum 클래스
 * 
 * Seo-JS0823
 */
public enum TokenType {
	ACCESS(1000L * 60 * 30), REFRESH(1000L * 60 * 60 * 24 * 7);
	
	private long expireTime;
	
	TokenType(long expireTime) {
		this.expireTime = expireTime;
	}
	
	public long getExpireTime() {
		return expireTime;
	}
}
