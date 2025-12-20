package com.findplan.controller.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.findplan.service.MemberService;
import com.findplan.transfer.request.SigninRequest;

@RestController
public class MemberApi {
	
	private final MemberService memberService;
	
	public MemberApi(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@PostMapping("/signin")
	public ResponseEntity<Map<String, String>> singin(@RequestBody SigninRequest request) {
		return ResponseEntity.ok(memberService.signin(request));
	}
	
}
