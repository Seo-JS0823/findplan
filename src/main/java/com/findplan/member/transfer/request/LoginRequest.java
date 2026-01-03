package com.findplan.member.transfer.request;

import com.findplan.member.entity.MemberEntity;

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
public class LoginRequest {

	private String email;
	
	private String password;
	
	public MemberEntity loginToEntity() {
		MemberEntity entity = MemberEntity.builder()
				.memberEmail(email)
				.memberPassword(password)
				.build();
		
		return entity;
	}
}
