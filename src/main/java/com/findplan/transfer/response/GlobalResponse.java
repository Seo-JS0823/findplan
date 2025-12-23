package com.findplan.transfer.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalResponse<T> {

	private boolean success;
	
	private T data;
	
	private String message;
	
	private String errorMessage;
	
	// 요청 성공에 대한 공통 메시지 응답 + 데이터와 함께
	public static <T> GlobalResponse<T> success(T data) {
		return new GlobalResponse<>(true, data, "success", null);
	}
	
	// 요청 성공에 대한 커스텀 메시지 응답 + 데이터와 함께
	public static <T> GlobalResponse<T> success(T data, String message) {
		return new GlobalResponse<>(true, data, message, null);
	}
	
	// 요청 성공에 대한 공통 메시지 응답 + 데이터 없음
	public static <T> GlobalResponse<T> success() {
		return new GlobalResponse<>(true, null, "success", null);
	}
	
	// 요청 성공에 대한 커스텀 메시지 응답 + 데이터 없음
	public static <T> GlobalResponse<T> success(String message) {
		return new GlobalResponse<>(true, null, message, null);
	}
	
	// 요청 실패에 대한 메시지 응답
	public static <T> GlobalResponse<T> error(ErrorMessage error) {
		return new GlobalResponse<>(false, null, null, error.getMessage());
	}
	
}
