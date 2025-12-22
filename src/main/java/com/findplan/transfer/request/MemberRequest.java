package com.findplan.transfer.request;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.findplan.entity.Member;
import com.findplan.entity.MemberDevice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

	private String email;
	
	private String password;
	
	private String nickname;
	
	private String userAgent;
	
	private String refreshToken;
	
	private String accessToken;
	
	private String ip;
	
	private Map<String, String> device;
	
	public Member toSigninEntity(PasswordEncoder passwordEncoder) {
		return Member.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.nickname(nickname)
				.createAt(LocalDateTime.now())
				.role("BRONZE")
				.build();
	}
	
	public MemberDevice toInsertedDeviceEntity(Member member) {
		return MemberDevice.builder()
				.deviceId(UUID.randomUUID().toString())
				.ip(ip)
				.model(device.get("MODEL"))
				.os(device.get("OS"))
				.browser(device.get("BROWSER"))
				.refreshToken(refreshToken)
				.member(member)
				.build();
	}
	
}
