package com.findplan.controller.api;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.findplan.service.MemberService;
import com.findplan.transfer.request.MemberRequest;
import com.findplan.utility.CookieName;
import com.findplan.utility.CookieUtil;

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
			deviceCookie = CookieUtil.createCookie(CookieName.DEVICE, login.get(CookieName.DEVICE.getName()));
		}
		if(login.containsKey("Authorization")) {
			accessCookie = CookieUtil.createCookie(CookieName.AUTHORIZATION, login.get(CookieName.AUTHORIZATION.getName()));
		}
		if(login.containsKey("RefreshToken")) {
			refreshCookie = CookieUtil.createCookie(CookieName.REFRESH_TOKEN, login.get(CookieName.REFRESH_TOKEN.getName()));
		}
		
		if(deviceCookie != null) {
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, deviceCookie)
					.header(HttpHeaders.SET_COOKIE, accessCookie)
					.header(HttpHeaders.SET_COOKIE, refreshCookie)
					.body(login);
		}
		
		if(refreshCookie != null) {
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, refreshCookie)
					.header(HttpHeaders.SET_COOKIE, accessCookie)
					.body(login);
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, accessCookie)
				.body(login);
	}
	
	@PatchMapping("/update")
	public ResponseEntity<Map<String, String>> updateTest(@RequestBody MemberRequest memberRequest) {
		return ResponseEntity.ok(memberService.memberUpdate(memberRequest));
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Map<String, String>> deleteTest(@RequestBody MemberRequest memberRequest) {
		Map<String, String> deleteResponse = memberService.memberDelete(memberRequest);
		
		String deleted = deleteResponse.get("delete");
		if(deleted.equals("true")) {
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, CookieUtil.clearCookie(CookieName.DEVICE))
					.header(HttpHeaders.SET_COOKIE, CookieUtil.clearCookie(CookieName.AUTHORIZATION))
					.header(HttpHeaders.SET_COOKIE, CookieUtil.clearCookie(CookieName.REFRESH_TOKEN))
					.body(Map.of("message", "회원 정보가 정상적으로 탈퇴처리 되었습니다."));
		}
		
		return ResponseEntity.ok(memberService.memberDelete(memberRequest));
	}
	
}
