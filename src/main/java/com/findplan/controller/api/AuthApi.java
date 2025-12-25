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
	 * Email 중복    : ErrorMessage.DUPLI_EMAIL
	 * Nickname 중복 : ErrorMessage.DUPLI_NICKNAME
	 * 성공 응답     : GlobalResponse.success(message)
	 */
	@PostMapping("/signin")
	public ResponseEntity<GlobalResponse<?>> signin(@RequestBody MemberRequest memberRequest) {
		return ResponseEntity.ok(authService.signin(memberRequest));
	}
	
	/*
	 * 로그인 컨트롤러
	 * 
	 * Email 불일치    : ErrorMessage.NOGIN_EMAIL_NOT_MATCH
	 * Password 불일치 : ErrorMessage.LOGIN_PASSWORD_NOT_MATCH
	 * 성공 응답       : GlobalResponse.TokenResponse
	 * 
	 * AccessToken  --> Authorization Header 저장
	 * RefreshToken --> Authorization-R Cookie 저장
	 * DeviceId     --> X-find-device Cookie 저장
	 * 
	 * RefreshToken 업데이트 O
	 */
	@PostMapping("/login")
	public ResponseEntity<GlobalResponse<?>> login(@RequestBody MemberRequest memberRequest, HttpServletRequest request) {
		GlobalResponse<?> response = authService.login(request, memberRequest);

		// 로그인 실패 응답 RefreshToken Cookie 삭제
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
	
	/*
	 * 회원정보 변경 컨트롤러
	 * 
	 * 사용자 Email & RefreshToken Email 불일치 : ErrorMessage.AUTH_NOT_MATCH
	 * 변경하려는 닉네임이 다른 사용자가 사용중 : ErrorMessage.DUPLI_NICKNAME
	 * 
	 * 성공 응답 : GlobalResponse.success(message)
	 * 
	 * RefreshToken 업데이트 X
	 */
	@PatchMapping("/update")
	public ResponseEntity<GlobalResponse<?>> update(@RequestBody MemberRequest memberRequest, HttpServletRequest request) {
		return ResponseEntity.ok(authService.memberInfoUpdate(memberRequest, request));
	}
	
}
