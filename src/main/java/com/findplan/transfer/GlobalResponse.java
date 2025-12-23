package com.findplan.transfer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalResponse<T> {

	private boolean success;
	
	private T data;
	
	private String message;
	
	private String errorMessage;
	
	private String location;
	
	private GlobalResponse(boolean success, T data, String message) {
		this.success = success;
		this.data = data;
		this.message = message;
	}
	
	private GlobalResponse(boolean success, String errorMessage) {
		this.success = success;
		this.errorMessage = errorMessage;
	}
	
	private GlobalResponse(boolean success, T data, String message, String errorMessage, String location) {
		this.success = success;
		this.data = data;
		this.message = message;
		this.errorMessage = errorMessage;
		this.location = location;
	}
	
	// 요청 성공에 대한 공통 메시지 응답 + 데이터와 함께
	public static <T> GlobalResponse<T> success(T data) {
		return new GlobalResponse<>(true, data, "success");
	}
	
	// 요청 성공에 대한 커스텀 메시지 응답 + 데이터와 함께
	public static <T> GlobalResponse<T> success(T data, String message) {
		return new GlobalResponse<>(true, data, message);
	}
	
	// 요청 성공에 대한 공통 메시지 응답 + 데이터 없음
	public static <T> GlobalResponse<T> success() {
		return new GlobalResponse<>(true, null, "success");
	}
	
	// 요청 성공에 대한 커스텀 메시지 응답 + 데이터 없음
	public static <T> GlobalResponse<T> success(String message) {
		return new GlobalResponse<>(true, null, message);
	}
	
	// 요청 성공시 Location Url 넣어서 응답
	public static <T> GlobalResponse<T> successLocation(String message, String location) {
		return new GlobalResponse<>(true, null, message, null, location);
	}
	
	// 요청 성공시 Location Url 과 데이터를 함께 넣어서 응답
	public static <T> GlobalResponse<T> successLocation(T data, String message, String location) {
		return new GlobalResponse<>(true, data, message, null, location);
	}
	
	// 요청 실패에 대한 메시지 응답
	public static <T> GlobalResponse<T> error(ErrorMessage error) {
		return new GlobalResponse<>(false, error.getMessage());
	}
	
	// 요청 실패에 대한 메시지 응답과 Location Url 넣어서 응답
	public static <T> GlobalResponse<T> errorLocation(ErrorMessage error, String location) {
		return new GlobalResponse<>(false, null, null, error.getMessage(), location);
	}
	
}
