package com.findplan.global.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ua_parser.Client;
import ua_parser.Parser;

@RequiredArgsConstructor
@Component
public class UserAgentParser {

	private final Parser parser;
	
	public String getOs(HttpServletRequest request) {
		Client c = init(request);
		
		return os(c);
	}
	
	public String getDevice(HttpServletRequest request) {
		Client c = init(request);
		
		return device(c);
	}
	
	public String getBrowser(HttpServletRequest request) {
		Client c = init(request);
		
		return browser(c);
	}
	
	private String browser(Client c) {
		String bsName = c.userAgent.family;
		String bsVer = Optional.ofNullable(c.userAgent.major).orElse("?");
		
		return String.format("%s-%s", bsName, bsVer);
	}
	
	private String device(Client c) {
		String deviceName = c.device.family;
		if(deviceName.equals("Other")) deviceName = "Desktop";
		
		return deviceName;
		
	}
	
	private String os(Client c) {
		String osName = c.os.family;
		String osVer = Optional.ofNullable(c.os.major).orElse("?");
		
		return String.format("%s-%s", osName, osVer);
	}
	
	private Client init(HttpServletRequest request) {
		return parser.parse(request.getHeader("User-Agent"));
	}
	
}
