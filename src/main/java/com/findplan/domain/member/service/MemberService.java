package com.findplan.domain.member.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.domain.member.model.DeviceEntity;
import com.findplan.domain.member.model.MemberEntity;
import com.findplan.domain.member.repository.MemberRepository;
import com.findplan.domain.member.transfer.MemberRequest;
import com.findplan.global.auth.JwtTokenProvider;
import com.findplan.global.auth.TokenType;
import com.findplan.global.error.ErrorCode;
import com.findplan.global.error.GlobalException;
import com.findplan.global.util.CookieName;
import com.findplan.global.util.CookieProvider;
import com.findplan.global.util.UserAgentParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	
	private final MemberRepository memberRepo;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtTokenProvider jwtTokenProvider;
	
	private final UserAgentParser parser;
	
	private final CookieProvider cookieProvider;
	
	/*
	 * 회원가입 로직
	 * 
	 * throw -> ErrorCode EMAIL_DUPLICATION, NICKNAME_DUPLICATION
	 */
	public void signup(MemberRequest signupRequest) {
		duplicateEmailCheck(signupRequest.getEmail());
		duplicateNicknameCheck(signupRequest.getNickname());
		
		MemberEntity signupEntity = signupRequest.signupToEntity(passwordEncoder);
		
		memberRepo.save(signupEntity);
	}
	
	/*
	 * 로그인 로직
	 * 
	 * throw -> ErrorCode LOGIN_NOT_MATCHES
	 * 
	 * 기존 기기 판단 기준
	 * 1. 클라이언트가 보낸 DeviceId가 해당 유저 Device 목록에 존재하나?
	 * 2. 존재한다면 해당 레코드의 DeviceInfo가 현재 요청과 일치하나?
	 * 3. 일치   : 기존 기기로 간주 -> RefreshToken 갱신
	 *    불일치 : DeviceInfo 필드 업데이트
	 *    
	 * 새 기기 판단 기준
	 * 1. 클라이언트가 보낸 DeviceId가 해당 유저 Device 목록에 존재하나?
	 * 2. 존재하지 않는다면 새로운 Device Entity 생성
	 */
	public void login(MemberRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
		// Null 이면 내부에서 GlobalException 던짐
		MemberEntity loginEntity = loginValidate(loginRequest.getEmail(), loginRequest.getPassword());
		
		// 클라이언트 요청에서 쿠키 (DeviceId) 가져오기
		String deviceId = cookieProvider.getCookieValue(CookieName.DEVICE, request);
		System.out.println("요청 디바이스 아이디 : " + deviceId);
		// 새 기기 로그인
		if(deviceId == null) newDeviceLogin(loginEntity, request, response);
		// 기존 기기 로그인
		else oldDeviceLogin(loginEntity, deviceId, request, response);
	}
	
	/*
	 * 기존 기기 로그인
	 */
	private void oldDeviceLogin(MemberEntity member, String deviceId, HttpServletRequest request, HttpServletResponse response) {
		// Device List 확인
		List<DeviceEntity> devices = member.getDevices();
		
		// 해당 유저의 Device List가 없는 경우 새 기기 로그인으로 다시 분기
		if(devices == null) {
			newDeviceLogin(member, request, response);
			return;
		}
		
		DeviceEntity device = devices.stream()
			.filter(d -> d.getDeviceId().equals(deviceId))
			.findFirst()
			.orElse(null);
		
		// 일치하는 디바이스 아이디가 없는 경우 새 기기 로그인으로 다시 분기
		if(device == null) {
			newDeviceLogin(member, request, response);
			return;
		}
		
		String refreshToken = jwtTokenProvider.createToken(member.getEmail(), TokenType.REFRESH);
		device.updateRefreshToken(refreshToken);
		
		cookieProvider.addCookie(CookieName.REFRESH, refreshToken, response);
	}
	
	/*
	 * 새 기기 로그인
	 * 
	 * 로그인 요청 유저의 DeviceId , RefreshToken 생성 및 DB 저장
	 */
	private void newDeviceLogin(MemberEntity member, HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = jwtTokenProvider.createToken(member.getEmail(), TokenType.REFRESH);
		
		String deviceId = UUID.randomUUID().toString();
		
		DeviceEntity device = DeviceEntity.builder()
				.deviceId(deviceId)
				.refreshToken(refreshToken)
				.os(parser.getOs(request))
				.device(parser.getDevice(request))
				.browser(parser.getBrowser(request))
				.ip(request.getRemoteAddr())
				.build();
		
		device.setMember(member);
		member.addDevice(device);
		
		cookieProvider.addCookie(CookieName.REFRESH, refreshToken, response);
		cookieProvider.addCookie(CookieName.DEVICE, deviceId, response);
	}
	
	/*
	 * 로그인 시 아이디, 패스워드 검증
	 * true  : 로그인 성공
	 * false : 로그인 실패
	 */
	private MemberEntity loginValidate(String email, String requestPassword) {
		MemberEntity findByEmail = memberRepo.findByEmailWithDevices(email);
		if(findByEmail == null) throw new GlobalException(ErrorCode.LOGIN_NOT_MATCHES);
		
		String encodedPassword = findByEmail.getPassword();
		
		if(!passwordEncoder.matches(requestPassword, encodedPassword)) {
			throw new GlobalException(ErrorCode.LOGIN_NOT_MATCHES);
		};
		
		return findByEmail;
	}
	
	/*
	 * 이메일 중복 체크
	 * true  : 중복 X
	 * false : 중복 O
	 */
	public void duplicateEmailCheck(String email) {
		if(memberRepo.existsByEmail(email)) {
			throw new GlobalException(ErrorCode.EMAIL_DUPLICATION);
		};
	}
	
	/*
	 * 닉네임 중복 체크
	 * true  : 중복 X
	 * false : 중복 O
	 */
	public void duplicateNicknameCheck(String nickname) {
		if(memberRepo.existsByNickname(nickname)) {
			throw new GlobalException(ErrorCode.NICKNAME_DUPLICATION);
		}
	}
	
}