package com.findplan.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.findplan.entity.Member;
import com.findplan.entity.MemberDevice;
import com.findplan.repository.MemberDeviceRepository;
import com.findplan.transfer.request.MemberRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberDeviceService {

	private final MemberDeviceRepository memberDeviceRepository;
	
	// 로그인한 기기가 새 기기인지 기존 기기인지 판단
	// 새 기기 로그인   : true
	// 기존 기기 로그인 : false
	public boolean loginDeviceExists(Long code, String deviceId) {
		List<MemberDevice> devices = memberDeviceRepository.findByMemberCode(code);
		
		for(MemberDevice device : devices) {
			if(deviceId.equals(device.getDeviceId())) {
				return false;
			}
		}
		
		return true;
	}
	
	public String deviceSave(MemberRequest memberRequest, Member loginMember) {
		MemberDevice insertedDevice = memberRequest.toInsertedDeviceEntity(loginMember);
		insertedDevice.loginAtUpdate();
		MemberDevice inserted = memberDeviceRepository.save(insertedDevice);
		
		return insertedDevice.getDeviceId();
	}
	
	public void refreshTokenUpdate(Member loginMember, String refreshToken, String deviceId) {
		MemberDevice updated = memberDeviceRepository.findByDeviceId(deviceId);
		updated.loginAtUpdate();
		updated.updateRefreshToken(refreshToken);
	}
	
}
