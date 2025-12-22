package com.findplan.utility;

import java.util.Map;
import java.util.Optional;

import ua_parser.Client;
import ua_parser.Parser;

public class UserAgentParser {
	
	public static Map<String, String> getDeviceInfo(String userAgent) {
		Client c = parse(userAgent);
		
		String model = getModel(c);
		String os = getOs(c);
		String browser = getBrowser(c);
		
		Map<String, String> deviceInfo = Map.of(
			"MODEL", model,
			"OS", os,
			"BROWSER", browser
		);
		
		return deviceInfo;
	}
	
	private static Client parse(String userAgent) {
		return new Parser().parse(userAgent);
	}
	
	private static String getBrowser(Client c) {
		String browser = c.userAgent.family;
		String browserMajor = Optional.ofNullable(c.userAgent.major).orElse("?");
		
		return String.format("%s_%s", browser, browserMajor);
	}
	
	private static String getOs(Client c) {
		String osName = c.os.family;
		String osMajor = Optional.ofNullable(c.os.major).orElse("?");
		
		return String.format("%s_%s", osName, osMajor);
	}
	
	private static String getModel(Client c) {
		return deviceModelMapper(c.device.family);
	}
	
	private static String deviceModelMapper(String deviceModel) {
		if(deviceModel.equals("Other")) {
			return "Desktop";
		}
		return deviceModel;
	}
	
}
