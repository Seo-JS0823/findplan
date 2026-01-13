package com.findplan.domain.member.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.findplan.domain.member.service.LoginHistoryService;
import com.findplan.domain.member.service.MemberService;
import com.findplan.domain.member.transfer.request.MemberRequest;
import com.findplan.domain.member.transfer.response.LoginHistoryResponse;
import com.findplan.domain.member.transfer.response.LoginResponse;
import com.findplan.global.common.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
	
	private final MemberService memberService;
	
	private final LoginHistoryService logHisService;
	
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<?>> signupResponse(
			@RequestBody MemberRequest signupRequest) {
		
		memberService.signup(signupRequest);
		
		return ResponseEntity.ok(ApiResponse.successRedirect("/"));
	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<?>> loginResponse(
			@RequestBody MemberRequest loginRequest,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		LoginResponse loginResponse = memberService.login(loginRequest, request, response);
		
		return ResponseEntity.ok(ApiResponse.successRedirect(loginResponse, "/main"));
	}
	
	@GetMapping("/logout")
	public ResponseEntity<ApiResponse<?>> logoutResponse(HttpServletRequest request, HttpServletResponse response) {
		memberService.logout(request, response);
		System.out.println("요청 잘 되었쑤!");
		return ResponseEntity.ok(ApiResponse.successRedirect("/"));
	}
	
	@PostMapping("/me")
	public ResponseEntity<ApiResponse<?>> rememberMeResponse(
			HttpServletRequest request,
			HttpServletResponse response) {
		
		// 자동 로그인 토큰이 없는 경우 fail 응답
		if(!memberService.rememberMe(request, response)) {
			return ResponseEntity.ok(ApiResponse.fail());
		}
		
		LoginResponse loginResponse = memberService.rememberMeLogin(request, response);
		
		return ResponseEntity.ok(ApiResponse.successRedirect(loginResponse, "/main"));
	}
	
	@GetMapping("/dupli-e")
	public ResponseEntity<ApiResponse<?>> emailCheck(@RequestParam("e") String email) {
		memberService.duplicateEmailCheck(email);
		return ResponseEntity.ok(ApiResponse.success());
	}
	
	@GetMapping("/dupli-n")
	public ResponseEntity<ApiResponse<?>> nicknameCheck(@RequestParam("n") String nickname) {
		memberService.duplicateNicknameCheck(nickname);
		return ResponseEntity.ok(ApiResponse.success());
	}
	
	@PatchMapping("/update")
	public ResponseEntity<ApiResponse<?>> updateResponse(
			@RequestBody MemberRequest updateRequest,
			HttpServletResponse response) {
		
		memberService.passwordUpdate(updateRequest, response);
		
		return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다. 모든 기기에서 로그아웃 되었으니 다시 로그인 해주세요."));
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<?>> deleteResponse(@RequestBody MemberRequest deleteRequest, HttpServletResponse response) {
		
		// 회원 탈퇴 시 memberWithDraw 내부에서 Authentication 객체에서 Email을 꺼내오는데 이 Email로
		// 멤버 조회가 되지 않으면 심각한 탈취라고 봐야하나?
		memberService.memberWithdraw(deleteRequest, response);
		
		return ResponseEntity.ok(ApiResponse.success());
	}
	
	@GetMapping("/history/login")
	public ResponseEntity<ApiResponse<?>> loginHistorySelect(HttpServletRequest request, HttpServletResponse response) {
		List<LoginHistoryResponse> histories = logHisService.findLoginHistoryAll();
		
		return ResponseEntity.ok(ApiResponse.success(histories));
	}
	
}