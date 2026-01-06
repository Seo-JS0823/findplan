package com.findplan.global.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
	private final boolean success;
	
	private final int status;
	
	private final String message;
	
	private final T data;
	
	private final String redirectUrl;
	
	public static <T> ApiResponse<T> success() {
		return new ApiResponse<>(true, 200, "success", null, null);
	}
	
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, 200, "success", data, null);
	}
	
	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>(true, 200, message, data, null);
	}
	
	public static <T> ApiResponse<T> successRedirect(String redirectUrl) {
		return new ApiResponse<>(true, 200, "success", null, redirectUrl);
	}
	
	public static <T> ApiResponse<T> successRedirect(T data, String redirectUrl) {
		return new ApiResponse<>(true, 200, "success", data, redirectUrl);
	}
	
	public static <T> ApiResponse<T> successRedirect(T data, String message, String redirectUrl) {
		return new ApiResponse<>(true, 200, message, data, redirectUrl);
	}
	
	public static <T> ApiResponse<T> error(int status, String message) {
		return new ApiResponse<>(false, status, message, null, null);
	}
	
	public static <T> ApiResponse<T> errorRedirect(int status, String message, String redirectUrl) {
		return new ApiResponse<>(false, status, message, null, redirectUrl);
	}
	
}
