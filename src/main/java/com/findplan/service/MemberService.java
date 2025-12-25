package com.findplan.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.entity.Member;
import com.findplan.repository.MemberRepository;
import com.findplan.transfer.request.MemberRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	public Member getMember(MemberRequest memberRequest) {
		Member member = memberRepository.findByEmail(memberRequest.getEmail());
		
		return member;
	}
	
	public boolean existsPassword(String requestPassword, String encodedPassword) {
		return passwordEncoder.matches(requestPassword, encodedPassword);
	}
	
	public boolean loginEmailAndPasswordValidate(MemberRequest memberRequest) {
		Member login = getMember(memberRequest);
		if(login == null) return false;
		
		boolean existsPassword = existsPassword(memberRequest.getPassword(), login.getPassword());
		if(!existsPassword) return false;
		
		return true;
	}
	
}
