package com.findplan.utility;

import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {

	public static String getCookieValue(CookieName cookie, HttpServletRequest request) {
		String cookieName = cookie.getCookieName();
		
		Cookie[] cookies = request.getCookies();
		
		for(Cookie c : cookies) {
			String name = c.getName();
			
			if(name.equals(cookieName)) return c.getValue();
		}
		
		return null;
	}
	
	public static String createCookie(CookieName cookie, String value, long expireTime) {
		ResponseCookie cookieResponse = ResponseCookie.from(cookie.getCookieName(), value)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(expireTime)
				.sameSite("strict")
				.build();
		
		return cookieResponse.toString();
	}
	
	public static String clearCookie(CookieName cookie) {
		ResponseCookie cookieResponse = ResponseCookie.from(cookie.getCookieName(), null)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(0)
				.sameSite("strict")
				.build();
		
		return cookieResponse.toString();
	}
	
	public static boolean containsCookie(CookieName cookie, HttpServletRequest request) {
		if(CookieUtil.getCookieValue(cookie, request) == null) return false;
		
		return true;
	}
	
}
