package com.findplan.utility;

public enum CookieName {
	DEVICE("X-find-device"), AUTHORIZATION("Authorization"), REFRESH_TOKEN("RefreshToken");
	
	private String name;
	
	CookieName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
