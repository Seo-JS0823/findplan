package com.findplan.controller.api;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.findplan.service.MemberService;
import com.findplan.transfer.request.MemberRequest;
import com.findplan.utility.CookieParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test/api/member")
@RequiredArgsConstructor
public class MemberApiTest {

	private final MemberService memberService;
	
	@PostMapping("/signin")
	public ResponseEntity<Map<String, String>> signinTest(@RequestBody MemberRequest request) {
		return ResponseEntity.ok(memberService.signin(request));
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> loginTest(HttpServletRequest request, @RequestBody MemberRequest memberRequest) {
		Map<String, String> login = memberService.login(request, memberRequest);
		String deviceCookie = null, accessCookie = null, refreshCookie = null;
		
		if(login.containsKey("X-find-device")) {
			deviceCookie = CookieParser.createCookie("X-find-device", login.get("X-find-device"));
		}
		if(login.containsKey("Authorization")) {
			accessCookie = CookieParser.createCookie("Authorization", login.get("Authorization"));
		}
		if(login.containsKey("RefreshToken")) {
			refreshCookie = CookieParser.createCookie("RefreshToken", login.get("RefreshToken"));
		}
		
		if(deviceCookie != null) {
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, deviceCookie)					
					.header(HttpHeaders.SET_COOKIE, accessCookie)
					.header(HttpHeaders.SET_COOKIE, refreshCookie)
					.body(login);
		}
		
		return ResponseEntity.ok(login);
	}
	
}
