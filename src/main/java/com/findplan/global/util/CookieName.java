package com.findplan.global.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookieName {

	DEVICE("DEV-FIND", 60 * 60 * 24 * 365, false),
	REFRESH("AUTH-R", 60 * 60 * 24 * 7, true),
	CLEAR_REFRESH("AUTH-R", 0, true),
	CLEAR_DEVICE("DEV-FIND", 0, true),
	DEVICE_REFRESH_PASS("U-DRP", 60 * 5, true),
	CLEAR_DEVICE_REFRESH_PASS("U-DRP", 0, true);
	
	private final String cookieName;
	
	private final int maxAge;
	
	private final boolean httpOnly;
}