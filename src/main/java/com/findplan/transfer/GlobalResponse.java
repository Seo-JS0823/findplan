package com.findplan.transfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalResponse<T> {

	private boolean success;
	
	private T data;
	
	private String message;
	
	private String errorMessage;
	
	private String location;
	
	private GlobalResponse(boolean success, T data, String message, String errorMessage, String location) {
		this.success = success;
		this.data = data;
		this.message = message;
		this.errorMessage = errorMessage;
		this.location = location;
	}
	
	// 성공 응답 : 응답 객체와 함께
	public static <T> GlobalResponse<T> success(T data) {
		return new GlobalResponse<>(true, data, "success", null, null);
	}
	
	// 성공 응답 : 응답 객체와 전용 메시지와 함께
	public static <T> GlobalResponse<T> success(T data, String message) {
		return new GlobalResponse<>(true, data, message, null, null);
	}
	
	// 성공 응답 : 기본 메시지
	public static <T> GlobalResponse<T> success() {
		return new GlobalResponse<>(true, null, "success", null, null);
	}
	
	// 성공 응답 : 전용 메시지만
	public static <T> GlobalResponse<T> success(String message) {
		return new GlobalResponse<>(true, null, message, null, null);
	}
	
	// 실패 응답 : 실패 시 전용 메시지만 
	public static <T> GlobalResponse<T> error(String errorMessage) {
		return new GlobalResponse<>(false, null, null, errorMessage, null);
	}
	
	// 실패 응답 : 실패 시 전용 메시지와 redirect URL 함께
	public static <T> GlobalResponse<T> errorLocation(String errorMessage, String location) {
		return new GlobalResponse<>(false, null, null, errorMessage, location);
	}
	
	// 성공 응답 : 응답 객체와 redirect URL 함께
	public static <T> GlobalResponse<T> successLocation(T data, String location) {
		return new GlobalResponse<>(true, data, "success", null, location);
	}
	
	// 성공 응답 : 전용 메시지와 redirect URL 함께
	public static <T> GlobalResponse<T> successLocation(String message, String location) {
		return new GlobalResponse<>(true, null, message, null, location);
	}
	
	// 성공 응답 : 응답 객체와 전용 메시지와 redirect URL 함께
	public static <T> GlobalResponse<T> successLocation(T data, String message, String location) {
		return new GlobalResponse<>(true, data, message, null, location);
	}
	
}
