package com.findplan.member.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.findplan.enums.CookieName;
import com.findplan.enums.TokenType;
import com.findplan.member.service.MemberService;
import com.findplan.member.transfer.request.LoginRequest;
import com.findplan.member.transfer.request.SignupRequest;
import com.findplan.security.service.AuthService;
import com.findplan.transfer.GlobalResponse;
import com.findplan.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

	private final MemberService memberService;
	
	private final AuthService authService;
	
	@PostMapping("/signup")
	public ResponseEntity<GlobalResponse<?>> signupHandler(@RequestBody SignupRequest signupRequest) {
		return ResponseEntity.ok(memberService.signup(signupRequest));
	}
	
	@PostMapping("/login")
	public ResponseEntity<GlobalResponse<?>> loginHandler(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
		GlobalResponse<?> response = memberService.login(loginRequest, request);
		
		Object tokenObjects = response.getData();
		if(tokenObjects == null) return ResponseEntity.ok(response);
		
		@SuppressWarnings("unchecked")
		Map<TokenType, String> tokens = (Map<TokenType, String>) tokenObjects;
		
		String refreshCookie = CookieUtil.createCookie(CookieName.REFRESH_TOKEN, tokens.get(TokenType.REFRESH), Duration.ofDays(7));
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshCookie)
				.body(response);
	}
	
	@GetMapping("/login/state")
	public ResponseEntity<GlobalResponse<?>> loginState(HttpServletRequest request) {
		return ResponseEntity.ok(authService.loginState(request));
	}
	
	@GetMapping("/logout")
	public ResponseEntity<GlobalResponse<?>> logout() {
		String clearRefreshToken = CookieUtil.clearCookie(CookieName.REFRESH_TOKEN);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, clearRefreshToken)
				.body(GlobalResponse.success());
	}
	
	@GetMapping("/dupli/email")
	public ResponseEntity<Boolean> duplicateEmail(@RequestParam("value") String value) {
		boolean response = memberService.duplicateEmail(value);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/dupli/nickname")
	public ResponseEntity<Boolean> duplicateNickname(@RequestParam("value") String value) {
		boolean response = memberService.duplicateNickname(value);
		
		return ResponseEntity.ok(response);
	}
	
}
