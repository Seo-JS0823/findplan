package com.findplan.domain.member.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.domain.member.model.DeviceEntity;
import com.findplan.domain.member.model.MemberEntity;
import com.findplan.domain.member.repository.LoginHistoryRepository;
import com.findplan.domain.member.repository.MemberRepository;
import com.findplan.domain.member.transfer.request.MemberRequest;
import com.findplan.domain.member.transfer.response.LoginResponse;
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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
	
	private final MemberRepository memberRepo;
	
	private final LoginHistoryRepository logHisRepo;
	
	private final PasswordEncoder passwordEncoder;
		
	private final UserAgentParser parser;
	
	private final CookieProvider cookieProvider;
	
	private final JwtTokenProvider jwtTokenProvider;
	
	private final DeviceService deviceService;
	
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		// Authentication에서 email 가져오기
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		// DeviceId, Refresh Cookie 가져오기
		String deviceId = cookieProvider.getCookieValue(CookieName.DEVICE, request);
		System.out.println("로그아웃 디바이스 아이디 : " + deviceId);
		String refreshToken = cookieProvider.getCookieValue(CookieName.REFRESH, request);
		System.out.println("로그아웃 리프레시 토큰 : " + refreshToken);
		
		// DeviceId로 DeviceEntity 조회
		MemberEntity member = memberRepo.findByEmail(email);
		
		List<DeviceEntity> devices = member.getDevices();
		DeviceEntity device = deviceService.deviceIdContains(deviceId, devices);
		if(device == null) {
			cookieProvider.clearSecurityCookie(response);
			System.out.println("여기냐?");
			throw new GlobalException(ErrorCode.UN_BAD_AUTHORIZATION);
		}
		
		if(!device.getRefreshToken().equals(refreshToken)) {
			cookieProvider.clearSecurityCookie(response);
			System.out.println("여긴가!?");
			throw new GlobalException(ErrorCode.UN_BAD_AUTHORIZATION);
		}
		
		device.initRefreshToken();
		
		cookieProvider.clearLogoutCookie(response);
	}
	
	/*
	 * 자동 로그인 체크 remember-me
	 */
	public boolean rememberMe(HttpServletRequest request, HttpServletResponse response) {
		String rememberMeToken = cookieProvider.getCookieValue(CookieName.REMEMBER_ME_TOKEN, request);
		if(rememberMeToken == null) return false;
		
		return true;
	}
	
	public LoginResponse rememberMeLogin(HttpServletRequest request, HttpServletResponse response) {
		// 사용자의 RefreshToken으로 email 가져오기
		String email = jwtTokenProvider.getEmailFromToken(cookieProvider.getCookieValue(CookieName.REFRESH, request));
		
		// 해당 유저의 Device 리스트에 DeviceId 있는지 확인
		String deviceId = cookieProvider.getCookieValue(CookieName.DEVICE, request);
		
		MemberEntity member = memberRepo.findByEmailWithDevices(email);
		if(member == null) {
			cookieProvider.clearSecurityCookie(response);
			throw new GlobalException(ErrorCode.REMEMBER_ME_LOGIN_NOT_MATCHES);
		}
		
		List<DeviceEntity> devices = member.getDevices();
		
		DeviceEntity device = deviceService.deviceIdContains(deviceId, devices);
		
		// 없으면 GlobalException
		if(device == null) throw new GlobalException(ErrorCode.REMEMBER_ME_LOGIN_NOT_MATCHES);
		
		String accessToken = loginTokenAction(response, email, device);
		
		// 있으면 LoginResponse 응답
		LoginResponse loginResponse = LoginResponse.builder()
				.accessToken(accessToken)
				.email(email)
				.nickname(member.getNickname())
				.build();
		
		logHisRepo.save(device.createLoginHistory());
		
		return loginResponse;
	}
	
	/*
	 * 로그인할 때 AccessToken 생성과 RefreshToken을 생성하고 DeviceEntity에 업데이트하며,
	 * 생성한 AccessToken을 반환함
	 */
	private String loginTokenAction(HttpServletResponse response, String email, DeviceEntity device) {
		String at = jwtTokenProvider.createToken(email, TokenType.ACCESS);
		cookieProvider.addCookie(CookieName.ACCESS, at, response);
		
		String rt = jwtTokenProvider.createToken(email, TokenType.REFRESH);
		cookieProvider.addCookie(CookieName.REFRESH, rt, response);
		
		device.updateRefreshToken(rt);
		
		return at;
	}
	
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
	public LoginResponse login(MemberRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
		// Null 이면 내부에서 GlobalException 던짐
		MemberEntity loginEntity = memberValidate(loginRequest.getEmail(), loginRequest.getPassword());
		
		// 탈퇴 처리중인 계정으로 로그인 했을 경우
		if(loginEntity.isDeleted() == true) {
			throw new GlobalException(ErrorCode.RETRACT_USER_LOGIN);
		}
		
		// 자동 로그인 기능이 체크되어 있는 경우
		if(loginRequest.isRememberMe() == true) {
			cookieProvider.addCookie(CookieName.REMEMBER_ME_TOKEN, "1", response);
		}
		
		// 클라이언트 요청에서 쿠키 (DeviceId) 가져오기
		String deviceId = cookieProvider.getCookieValue(CookieName.DEVICE, request);
		
		// 새 기기 로그인
		if(deviceId == null) newDeviceLogin(loginEntity, request, response);
		// 기존 기기 로그인
		else oldDeviceLogin(loginEntity, deviceId, request, response);
		
		// AccessToken 발급
		String accessToekn = jwtTokenProvider.createToken(loginEntity.getEmail(), TokenType.ACCESS);
		
		cookieProvider.addCookie(CookieName.ACCESS, accessToekn, response);
		
		return LoginResponse.builder()
				.accessToken(accessToekn)
				.email(loginEntity.getEmail())
				.nickname(loginEntity.getNickname())
				.build();
	}
	
	/*
	 * 기존 기기 로그인
	 * 
	 * RefreshToken 생성 및 DB 업데이트
	 */
	private void oldDeviceLogin(MemberEntity member, String deviceId, HttpServletRequest request, HttpServletResponse response) {
		// Device List 확인
		List<DeviceEntity> devices = member.getDevices();
		
		// 해당 유저의 Device List가 없는 경우 새 기기 로그인으로 다시 분기
		if(devices == null) {
			newDeviceLogin(member, request, response);
			return;
		}
		
		// 요청 DeviceId 와 DeviceEntity 레코드 일치 확인
		DeviceEntity device = deviceService.deviceIdContains(deviceId, devices);
		
		// 일치하는 디바이스 아이디가 없는 경우 새 기기 로그인으로 다시 분기
		if(device == null) {
			newDeviceLogin(member, request, response);
			return;
		}
		
		// 기기 메타데이터 변경 감지
		DeviceEntity deviceUpdate = userAgentEquals(device, request);
		
		// 리프레시 토큰 업데이트
		String refreshToken = jwtTokenProvider.createToken(member.getEmail(), TokenType.REFRESH);
		deviceUpdate.updateRefreshToken(refreshToken);
		
		// 로그인 이력 저장
		logHisRepo.save(deviceUpdate.createLoginHistory());
		
		// 리프레시 토큰 쿠키를 응답에 추가
		cookieProvider.addCookie(CookieName.REFRESH, refreshToken, response);
	}
	
	/*
	 * 새 기기 로그인
	 * 
	 * 로그인 요청 유저의 DeviceId , RefreshToken 생성 및 DB 저장
	 */
	private void newDeviceLogin(MemberEntity member, HttpServletRequest request, HttpServletResponse response) {
		// 요청자의 리프레시 토큰 생성
		String refreshToken = jwtTokenProvider.createToken(member.getEmail(), TokenType.REFRESH);
		
		// 디바이스 아이디 생성
		String deviceId = UUID.randomUUID().toString();
		
		// 디바이스 엔티티 생성 및 저장
		DeviceEntity device = DeviceEntity.builder()
				.deleted(false)
				.deviceId(deviceId)
				.refreshToken(refreshToken)
				.os(parser.getOs(request))
				.device(parser.getDevice(request))
				.browser(parser.getBrowser(request))
				.ip(request.getRemoteAddr())
				.build();
		
		device.setMember(member);
		member.addDevice(device);
		
		// 로그인 이력 저장
		logHisRepo.save(device.createLoginHistory());
		
		// 액세스 토큰, 리프레시 토큰 쿠키를 응답에 추가
		cookieProvider.addCookie(CookieName.REFRESH, refreshToken, response);
		cookieProvider.addCookie(CookieName.DEVICE, deviceId, response);
	}
	
	/*
	 * 로그인 시 요청자 기기 정보 변경 감지
	 */
	private DeviceEntity userAgentEquals(DeviceEntity deviceEntity, HttpServletRequest request) {
		String os = parser.getOs(request);
		String device = parser.getDevice(request);
		String browser = parser.getBrowser(request);
		
		String oldOs = deviceEntity.getOs();
		String oldDevice = deviceEntity.getDevice();
		String oldBrowser = deviceEntity.getBrowser();
		
		if(!os.equals(oldOs))
			deviceEntity.updateOs(os);
		
		if(!device.equals(oldDevice))
			deviceEntity.updateDevice(device);
		
		if(!browser.equals(oldBrowser))
			deviceEntity.updateBrowser(oldBrowser);
		
		return deviceEntity;
	}
	
	/*
	 * 로그인 시 아이디, 패스워드 검증
	 * true  : 로그인 성공
	 * false : 로그인 실패
	 */
	private MemberEntity memberValidate(String email, String requestPassword) {
		MemberEntity findByEmail = memberRepo.findByEmailWithDevices(email);
		if(findByEmail == null) {
			log.warn("로그인 시도 중 존재하는 회원 아이디가 아님");
			throw new GlobalException(ErrorCode.LOGIN_NOT_MATCHES);
		}
		
		String encodedPassword = findByEmail.getPassword();
		
		if(!passwordEncoder.matches(requestPassword, encodedPassword)) {
			log.warn("로그인 시도 중 비밀번호가 일치하지 않음");
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
	
	/*
	 * 패스워드 변경
	 */
	public void passwordUpdate(MemberRequest updateRequest, HttpServletResponse response) {
		MemberEntity validate = memberValidate(updateRequest.getEmail(), updateRequest.getPassword());
		
		if(passwordEncoder.matches(updateRequest.getNewPassword(), validate.getPassword())) {
			throw new GlobalException(ErrorCode.PASSWORD_SAME_UPDATE); 
		}
		
		validate.updatePassword(passwordEncoder, updateRequest.getNewPassword());
		
		validate.getDevices().stream()
						.forEach(DeviceEntity::delete);
		
		cookieProvider.addCookie(CookieName.CLEAR_REFRESH, null, response);
	}
	
	/*
	 * 회원탈퇴 : Soft Delete
	 * 
	 * 3일 뒤 삭제 스케줄러 도입 예정
	 */
	public void memberWithdraw(MemberRequest deleteRequest, HttpServletResponse response) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		MemberEntity member = memberRepo.findByEmail(email);		
		if(member == null) {
			cookieProvider.clearSecurityCookie(response);
			throw new GlobalException(ErrorCode.IDENTITY_NOT_FOUND);			
		}
		
		String oldPassword = deleteRequest.getPassword();
		String encodedPassword = member.getPassword();
		
		if(!passwordEncoder.matches(oldPassword, encodedPassword)) {
			throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCH);
		}
		
		member.withdraw();
		
		cookieProvider.clearSecurityCookie(response);
	}
	
}