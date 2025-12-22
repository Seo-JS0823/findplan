package com.findplan.transfer.response;

import java.util.Map;

import com.findplan.utility.CookieName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Builder
public class AuthResponse {

	private String location;
	
	private String device;
	
	private String accessToken;
	
	private String refreshToken;
	
	public AuthResponse(String location) {
		this.location = location;
	}
	
	public Map<String, String> getResponse() {
		if(device != null) return newLogin();
		
		else if (refreshToken != null && accessToken != null) return oldLoginRefresh();
		
		else if (accessToken != null) return oldLoginNotRefresh();
		
		else return location();
	}
	
	// Location만 응답
	private Map<String, String> location() {
		return Map.of("location", location);
	}
	
	// 새로운 기기에서 로그인 하였음.
	private Map<String, String> newLogin() {
		return Map.of(
			"location", location,
			CookieName.DEVICE.getName(), device,
			CookieName.AUTHORIZATION.getName(), accessToken,
			CookieName.REFRESH_TOKEN.getName(), refreshToken
		);
	}
	
	// 기존 기기에서 로그인 하였으나 리프레시 토큰이 만료된 상태
	private Map<String, String> oldLoginRefresh() {
		return Map.of(
			"location", location,
			CookieName.AUTHORIZATION.getName(), accessToken,
			CookieName.REFRESH_TOKEN.getName(), refreshToken
		);
	}
	
	// 기존 기기에서 로그인하였고 리프레시 토큰이 만료되지 않음
	private Map<String, String> oldLoginNotRefresh() {
		return Map.of(
			"location", location,
			CookieName.AUTHORIZATION.getName(), accessToken
		);
	}
	
}
