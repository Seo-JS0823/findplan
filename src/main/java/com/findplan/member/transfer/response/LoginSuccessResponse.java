package com.findplan.member.transfer.response;

public class LoginSuccessResponse {

	private String nickname;
	
	public LoginSuccessResponse() {}
	
	public LoginSuccessResponse(String nickname) {
		this.nickname = nickname;
	}
	
	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }
	
}
