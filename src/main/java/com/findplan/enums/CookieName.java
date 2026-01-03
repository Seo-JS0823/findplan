package com.findplan.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookieName {
	DEVICE("X-DEVICE-ID"),
	REFRESH_TOKEN("X-AUTH-REFRESH");
	
	private final String value;
}
