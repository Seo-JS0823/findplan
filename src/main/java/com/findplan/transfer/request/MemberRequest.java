package com.findplan.transfer.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberRequest {

	private String email;
	
	private String password;
	
	private String nickname;
	
}
