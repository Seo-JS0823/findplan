package com.findplan.util;

import java.time.Duration;

import org.springframework.http.ResponseCookie;

import com.findplan.enums.CookieName;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {

	public static String getCookieValue(CookieName cookie, HttpServletRequest request) {
		String cookieName = cookie.getValue();
		
		Cookie[] cookies = request.getCookies();
		
		for(Cookie c : cookies) {
			if(c.getName().equals(cookieName)) {
				return c.getValue();
			}
		}
		
		return null;
	}
	
	public static String createCookie(CookieName cookie, String value, Duration maxAge) {
		ResponseCookie cookieResponse = ResponseCookie.from(cookie.getValue(), value)
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(maxAge)
			.sameSite("strict")
			.build();
		
		return cookieResponse.toString();
	}
	
	public static String clearCookie(CookieName cookie) {
		ResponseCookie cookieResponse = ResponseCookie.from(cookie.getValue(), null)
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