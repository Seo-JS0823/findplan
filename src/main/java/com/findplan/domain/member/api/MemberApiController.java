package com.findplan.domain.member.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.findplan.domain.member.service.MemberService;
import com.findplan.domain.member.transfer.MemberRequest;
import com.findplan.global.common.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
	
	private final MemberService memberService;
	
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<?>> signupResponse(@RequestBody MemberRequest signupRequest) {
		memberService.signup(signupRequest);
		
		return ResponseEntity.ok(ApiResponse.successRedirect("/"));
	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<?>> loginResponse(
			@RequestBody MemberRequest loginRequest,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		memberService.login(loginRequest, request, response);
		
		return ResponseEntity.ok(ApiResponse.success());
	}
	
}