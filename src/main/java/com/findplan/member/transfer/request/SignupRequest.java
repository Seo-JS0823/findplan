package com.findplan.member.transfer.request;

import java.time.LocalDateTime;

import com.findplan.member.entity.MemberEntity;
import com.findplan.member.entity.MemberRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

	private String email;
	
	private String password;
	
	private String nickname;
	
	public MemberEntity signupToEntity() {
		MemberEntity entity = MemberEntity.builder()
				.memberEmail(email)
				.memberNickname(nickname)
				.memberPassword(password)
				.role(MemberRole.MEMBER)
				.createAt(LocalDateTime.now())
				.build();
		
		return entity;
	}
	
}
