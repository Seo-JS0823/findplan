package com.findplan.domain.member.transfer;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.findplan.domain.member.MemberRole;
import com.findplan.domain.member.model.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequest {

	private String email;
	
	private String password;
	
	private String nickname;
	
	public MemberEntity signupToEntity(PasswordEncoder passwordEncoder) {
		return MemberEntity.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.nickname(nickname)
				.createAt(LocalDateTime.now())
				.role(MemberRole.MEMBER)
				.build();
	}
	
	public MemberEntity loginToEntity() {
		return MemberEntity.builder()
				.email(email)
				.password(password)
				.build();
	}
	
}
