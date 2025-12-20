package com.findplan.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.model.Member;
import com.findplan.repository.MemberRepository;
import com.findplan.transfer.request.SigninRequest;

@Service
public class MemberService {
	
	private final MemberRepository memberRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	public MemberService(
			MemberRepository memberRepository,
			PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public Map<String, String> signin(SigninRequest request) {
		Member member = request.signinEntity(passwordEncoder);
		if(memberRepository.save(member) == null) {
			return Map.of("message", "회원가입 실패");
		}
			return Map.of("message", "회원가입 완료");
	}
	
}
