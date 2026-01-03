package com.findplan.security.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.findplan.enums.CookieName;
import com.findplan.enums.TokenType;
import com.findplan.security.CustomDetails;
import com.findplan.security.jwt.JwtTokenProvider;
import com.findplan.transfer.GlobalResponse;
import com.findplan.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final JwtTokenProvider jwtTokenProvider;
	
	public GlobalResponse<?> loginState(HttpServletRequest request) {
		// Authorization Header 검증
		String authorization = request.getHeader("Authorization");
		String token = null;
		if(authorization != null) {
			token = authorization.substring(7);
		}
		
		if(token != null) {
			if(jwtTokenProvider.validateToken(token)) {
				Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				if(user instanceof CustomDetails) {
					return GlobalResponse.success();
				}
			}
		}
		
		// Authorization Header 비어있을 시
		String refreshToken = CookieUtil.getCookieValue(CookieName.REFRESH_TOKEN, request);
		if(refreshToken != null) {
			if(jwtTokenProvider.validateToken(refreshToken)) {
				String accessToken = jwtTokenProvider.createToken(jwtTokenProvider.getUsernameFromToken(refreshToken), TokenType.ACCESS);
				Map<TokenType, String> tokenResponse = new HashMap<>();
				tokenResponse.put(TokenType.ACCESS, accessToken);
				
				return GlobalResponse.success(tokenResponse);
			}
		}
		
		return GlobalResponse.error("로그인 상태가 아닙니다.");
	}
}
