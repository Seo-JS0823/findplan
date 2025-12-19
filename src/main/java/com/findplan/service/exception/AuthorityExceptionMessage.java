package com.findplan.service.exception;

/*
 * 2025-12-20
 * AuthorityEntity를 다룰 때 일어나는 예외 상황에 대한 메시지 객체
 * 
 * Seo-JS0823
 */
public enum AuthorityExceptionMessage implements ExceptionMessageOperator {
	NOT_SAVED("권한 정보를 저장하지 못했습니다.");
	
	private final String message;
	
	AuthorityExceptionMessage(String message) {
		this.message = message;
	}

	@Override
	public RuntimeException exception() {
		return new AuthorityException(this.message);
	}
	
	
}
