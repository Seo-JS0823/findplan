package com.findplan.domain.member.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.findplan.domain.member.model.DeviceEntity;
import com.findplan.domain.member.model.MemberEntity;
import com.findplan.domain.member.repository.MemberRepository;
import com.findplan.global.error.ErrorCode;
import com.findplan.global.error.GlobalException;
import com.findplan.global.util.CookieName;
import com.findplan.global.util.CookieProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {
	
	private final CookieProvider cookieProvider;
	
	private final MemberRepository memberRepo;
	
	public DeviceEntity deviceIdContains(String deviceId, List<DeviceEntity> devices) {
		DeviceEntity device = devices.stream()
				.filter(d -> d.getDeviceId().equals(deviceId))
				.findFirst()
				.orElse(null);
		
		if(device == null) return null;
		
		return device;
	}
	
	/*
	 * 요청자의 DeviceId와 RefreshToken이 같은지 검사
	 * 
	 * 해당 메서드는 POST, PATCH, DELETE 메서드에서 무조건 사용
	 * GET 요청은 민감 정보 조회에서만 사용
	 * 
	 * 나중에 WebConfig --> Interceptor 사용해서 처리
	 */
	public void deviceIdAndRefreshTokenValidate(HttpServletRequest request, HttpServletResponse response) {
		// U-DRP 쿠키가 있으면 검사 패스 
		if(cookieProvider.getCookieValue(CookieName.DEVICE_REFRESH_PASS, request) != null) {
			return;
		}
		
		// U-DRP 쿠키가 없으면 검사 후 생성
		if(deviceIdAndRefreshTokenValidateImpl(request)) {
			System.out.println("Device Id And RefreshToken 일치검증 성공");
			
			cookieProvider.addCookie(CookieName.DEVICE_REFRESH_PASS, "1", response);
			return;
		}
		
		// 검사 통과하지 못하면 권한 부적합
		throw new GlobalException(ErrorCode.NOT_AUTHORIZATION);
	}
	
	private boolean deviceIdAndRefreshTokenValidateImpl(HttpServletRequest request) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("DRV 검사 요청자 {}", email);
		
		String deviceId = cookieProvider.getCookieValue(CookieName.DEVICE, request);
		log.info("DRV Device Id {}", deviceId);
		String refreshToken = cookieProvider.getCookieValue(CookieName.REFRESH, request);
		log.info("DRV RefreshToken {}", refreshToken);
		
		MemberEntity member = memberRepo.findByEmailWithDevices(email);
		if(member == null) {
			log.info("멤버가 널이네!?");
			return false;
		}
		
		List<DeviceEntity> devices = member.getDevices();
		
		DeviceEntity device = devices.stream()
				.filter(d -> d.getDeviceId().equals(deviceId) && d.getRefreshToken().equals(refreshToken))
				.findFirst()
				.orElse(null);
		if(device == null) {
			log.info("디바이스 엔티티가 널이네!?");
			return false;
		}
		
		return true;
	}
	
}















