package com.findplan.domain.member.transfer.request;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.findplan.domain.member.model.MemberEntity;
import com.findplan.domain.member.model.MemberRole;

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
	
	private String newPassword;
	
	@Builder.Default
	private boolean rememberMe = false;
	
	public MemberEntity signupToEntity(PasswordEncoder passwordEncoder) {
		return MemberEntity.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.nickname(nickname)
				.createdAt(LocalDateTime.now())
				.deleted(false)
				.roles(MemberRole.MEMBER)
				.build();
	}
	
	public MemberEntity loginToEntity() {
		return MemberEntity.builder()
				.email(email)
				.password(password)
				.build();
	}
	
}
