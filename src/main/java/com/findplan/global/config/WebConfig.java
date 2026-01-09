package com.findplan.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.findplan.global.auth.DeviceCheckInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final DeviceCheckInterceptor deviceCheckInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(deviceCheckInterceptor)
			.addPathPatterns("/api/member/**")
			.excludePathPatterns(
				"/api/member/login", "/api/member/signup",
				"/api/member/dupli-e", "/api/member/dupli-n"
			);
		
	}
	
}
