package com.findplan.utility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookieName {

	DEVICE("X-find-device"),
	REFRESH_TOKEN("Authorization-R");
	
	private final String cookieName;
}
