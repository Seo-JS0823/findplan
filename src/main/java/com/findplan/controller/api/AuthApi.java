package com.findplan.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.findplan.service.AuthService;
import com.findplan.transfer.request.MemberRequest;
import com.findplan.transfer.response.GlobalResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthApi {

	private final AuthService authService;
	
	@PostMapping("/signin")
	public ResponseEntity<GlobalResponse<?>> signin(@RequestBody MemberRequest memberRequest) {
		return ResponseEntity.ok(authService.signin(memberRequest));
	}
}
