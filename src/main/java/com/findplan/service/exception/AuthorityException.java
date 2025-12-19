package com.findplan.service.exception;

/*
 * 2025-12-20
 * AutorityEntity 다룰 때 일어날 수 있는 예외 상황에 대한 상위 객체
 * MessageOperator 를 통해 상세한 에러 메시지 출력을 전제로 클래스명을 이렇게 지었음.
 * 
 * Seo-JS0823
 */
public class AuthorityException extends RuntimeException {
	
	public AuthorityException() {}
	
	public AuthorityException(String message) {
		super(message);
	}
	
}
