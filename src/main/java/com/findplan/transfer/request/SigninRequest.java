package com.findplan.transfer.request;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.findplan.model.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * 2025-12-20
 * SinginRequest :: 회원가입 요청 객체
 * 
 * Seo-JS0823
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SigninRequest {
	private String email;
	
	private String password;
	
	private String nickname;
	
	/**
	 * signinEntity
	 * return: Member Entity
	 * 
	 * 회원가입 시 Insert를 위해 Entity로 변환하는 로직
	 * memberService에 의해 호출되며 PasswordEncoder를 주입받아 Password를 인코딩 한 후 엔티티로 바꿈
	 * 
	 * 기본 권한은 BRONZE
	 * 
	 * Seo-JS0823
	 */
	public Member signinEntity(PasswordEncoder passwordEncoder) {
		return Member.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.nickname(nickname)
				.role("BRONZE")
				.build();
	}
}
