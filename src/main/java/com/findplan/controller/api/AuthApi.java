package com.findplan.controller.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.findplan.service.AuthService;
import com.findplan.transfer.GlobalResponse;
import com.findplan.transfer.request.MemberRequest;
import com.findplan.transfer.response.TokenResponse;
import com.findplan.utility.CookieName;
import com.findplan.utility.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthApi {

	private final AuthService authService;
	
	/*
	 * 회원가입 컨트롤러
	 * 
	 * AuthService --> login Method
	 */
	@PostMapping("/signin")
	public ResponseEntity<GlobalResponse<?>> signin(@RequestBody MemberRequest memberRequest) {
		return ResponseEntity.ok(authService.signin(memberRequest));
	}
	
	@PostMapping("/login")
	public ResponseEntity<GlobalResponse<?>> login(@RequestBody MemberRequest memberRequest, HttpServletRequest request) {
		System.out.println("로그인 요청 Request Member : " + memberRequest);
		GlobalResponse<?> response = authService.login(request, memberRequest);

		if(!response.isSuccess()) {
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, CookieUtil.clearCookie(CookieName.REFRESH_TOKEN))
					.body(response);
		}
		
		HttpHeaders headers = new HttpHeaders();
		
		if(response.getData() instanceof TokenResponse tokenResponse) {
			if(tokenResponse.getDeviceId() != null) {
				String deviceCookie =
					CookieUtil.createCookie(CookieName.DEVICE, tokenResponse.getDeviceId(), 1000L * 60 * 60 * 24);
				headers.add(HttpHeaders.SET_COOKIE, deviceCookie);
			}
			
			if(tokenResponse.getRefreshToken() != null) {
				String refreshTokenCookie =
					CookieUtil.createCookie(CookieName.REFRESH_TOKEN, tokenResponse.getRefreshToken(), 1000L * 60 * 60 * 24 * 3);				
				headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie);
			}
		}
		
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@PatchMapping("/update")
	public ResponseEntity<GlobalResponse<?>> update(@RequestBody MemberRequest memberRequest, HttpServletRequest request) {
		return ResponseEntity.ok(authService.memberInfoUpdate(memberRequest, request));
	}
	
}
