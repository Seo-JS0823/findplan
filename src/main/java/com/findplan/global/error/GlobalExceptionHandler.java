package com.findplan.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.findplan.global.common.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(GlobalException.class)
	protected ResponseEntity<ApiResponse<Void>> handleGlobalException(GlobalException e) {
		log.error("GlobalException : {}", e.getErrorCode().getMessage());
		ErrorCode errorCode = e.getErrorCode();
		
		return ResponseEntity
						.status(errorCode.getStatus())
						.body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage()));
	}
}
