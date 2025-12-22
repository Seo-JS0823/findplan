package com.findplan.utility;

import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {

	public static String getCookieValue(HttpServletRequest request, CookieName cookieName) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) return null;
		
		for(Cookie cookie : cookies) {
			if(cookieName.getName().equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public static String createCookie(CookieName cookieName, String value) {
		ResponseCookie cookie = ResponseCookie.from(cookieName.getName(), value)
        .httpOnly(true)
        // 배포 시 true로 변경
        .secure(false)
        .path("/")
        .maxAge(7 * 24 * 60 * 60)
        .sameSite("strict")
        .build();
		return cookie.toString();
	}
	
	public static String clearCookie(CookieName cookieName) {
		ResponseCookie cookie = ResponseCookie.from(cookieName.getName(), null)
				.httpOnly(true)
				// 배포 시 true로 변경
				.secure(false)
				.path("/")
				.maxAge(0)
				.sameSite("strict")
				.build();
		
		return cookie.toString();
	}
	
}
