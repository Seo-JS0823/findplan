package com.findplan.global.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.findplan.global.util.CookieName;
import com.findplan.global.util.CookieProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	
	private final CookieProvider cookieProvider;
	
	@GetMapping("/")
	public String introView() {
		return "intro";
	}
	
	@GetMapping("/main")
	public String mainView(HttpServletRequest request, Model model) {
		String accessToken = cookieProvider.getCookieValue(CookieName.ACCESS, request);
		
		return "main";
	}
	
}
