package com.findplan.global.auth.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.findplan.domain.member.service.DeviceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceCheckInterceptor implements HandlerInterceptor {

	private final DeviceService deviceService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		deviceService.deviceIdAndRefreshTokenValidate(request, response);
		
		return true;
	}
	
}
