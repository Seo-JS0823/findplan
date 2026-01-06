package com.findplan.global.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieProvider {
	
	// 쿠키 생성 + 응답 함께 (컨트롤러에서 사용 권장)
	public void addCookie(CookieName cookieName, String value, HttpServletResponse response) {
		response.addHeader(HttpHeaders.SET_COOKIE, createCookie(cookieName, value));
	}
	
	public String getCookieValue(CookieName cookieName, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) return null;
		
		Cookie search = searchCookie(cookieName, cookies);
		if(search == null) return null;
		
		return search.getValue();
	}
	
	private String createCookie(CookieName cookieName, String value) {
		ResponseCookie cookie = ResponseCookie.from(cookieName.getCookieName(), value)
				.path("/")
				.httpOnly(cookieName.isHttpOnly())
				.secure(false)
				.maxAge(cookieName.getMaxAge())
				.sameSite("strict")
				.build();
		
		return cookie.toString();
	}
	
	private Cookie searchCookie(CookieName cookieName, Cookie[] cookies) {
		for(Cookie c : cookies) {
			if(c.getName().equals(cookieName.getCookieName())) {
				return c;
			}
		}
		return null;
	}
	
}
