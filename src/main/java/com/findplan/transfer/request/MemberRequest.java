package com.findplan.transfer.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRequest {

	private String email;
	
	private String password;
	
	private String nickname;
	
}
