package com.findplan.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.entity.Member;
import com.findplan.entity.Role;
import com.findplan.repository.MemberRepository;
import com.findplan.transfer.request.MemberRequest;
import com.findplan.transfer.response.ErrorMessage;
import com.findplan.transfer.response.GlobalResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final MemberRepository memberRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	public GlobalResponse<?> signin(MemberRequest memberRequest) {
		String email = memberRequest.getEmail();
		boolean existsEmail = memberRepository.existsByEmail(email);
		if(existsEmail) return GlobalResponse.error(ErrorMessage.DUPLI_EMAIL);
		
		String nickname = memberRequest.getNickname();
		boolean existsNickname = memberRepository.existsByNickname(nickname);
		if(existsNickname) return GlobalResponse.error(ErrorMessage.DUPLI_NICKNAME);
		
		String encodedPassword = passwordEncoder.encode(memberRequest.getPassword());
		
		Member member = Member.builder()
				.email(email)
				.nickname(nickname)
				.password(encodedPassword)
				.role(Role.MEMBER)
				.signinDate(LocalDateTime.now())
				.build();
		
		memberRepository.save(member);
		
		return GlobalResponse.success("회원가입이 완료되었습니다.");
	}
	
}
