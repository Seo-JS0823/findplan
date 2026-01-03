package com.findplan.member.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.enums.TokenType;
import com.findplan.member.entity.MemberEntity;
import com.findplan.member.repository.MemberRepository;
import com.findplan.member.transfer.request.LoginRequest;
import com.findplan.member.transfer.request.SignupRequest;
import com.findplan.security.jwt.JwtTokenProvider;
import com.findplan.transfer.GlobalResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtTokenProvider jwtTokenProvider;
	
	public GlobalResponse<?> signup(SignupRequest signupRequest) {
		MemberEntity signupEntity = signupRequest.signupToEntity();
		signupEntity.signupPasswordEncode(passwordEncoder);
		
		memberRepository.save(signupEntity);
		
		return GlobalResponse.success();
	}
	
	public GlobalResponse<?> login(LoginRequest loginRequest, HttpServletRequest request) {
		MemberEntity loginEntity = memberRepository.findByMemberEmail(loginRequest.getEmail()).orElse(null);
		
		// 아이디, 패스워드 검증
		if(!loginValidate(loginEntity, loginRequest)) return GlobalResponse.error("아이디 또는 패스워드가 일치하지 않습니다.");
		
		// 쿠키 DeviceId 유무 확인
		
		
		Map<TokenType, String> tokens = jwtTokenProvider.createTokenAll(loginEntity.getMemberEmail());
		
		return GlobalResponse.success(tokens, "로그인 요청 성공");
	}
	
	private boolean loginValidate(MemberEntity entity, LoginRequest loginRequest) {
		if(entity == null) return false;
		
		String requestPassword = loginRequest.getPassword();
		String encodedPassword = entity.getMemberPassword();
		
		return passwordEncoder.matches(requestPassword, encodedPassword);
	}
	
	public boolean duplicateEmail(String email) {
		return memberRepository.existsByMemberEmail(email);
	}
	
	public boolean duplicateNickname(String nickname) {
		return memberRepository.existsByMemberNickname(nickname);
	}
	
}
