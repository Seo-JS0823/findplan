package com.findplan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.auth.JwtTokenProvider;
import com.findplan.auth.MemberDetails;
import com.findplan.auth.TokenType;
import com.findplan.entity.Device;
import com.findplan.entity.Member;
import com.findplan.entity.Role;
import com.findplan.repository.DeviceRepository;
import com.findplan.repository.MemberRepository;
import com.findplan.transfer.ErrorMessage;
import com.findplan.transfer.GlobalResponse;
import com.findplan.transfer.request.MemberRequest;
import com.findplan.transfer.response.TokenResponse;
import com.findplan.utility.CookieName;
import com.findplan.utility.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ua_parser.Client;
import ua_parser.Parser;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final JwtTokenProvider jwtTokenProvider;
	
	private final MemberRepository memberRepository;
	
	private final DeviceRepository deviceRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final MemberService memberService;
	
	private final Parser uaParser;
	
	/*
	 * Seo-JS0823
	 *  
	 * 회원가입
	 */
	public GlobalResponse<?> signin(MemberRequest memberRequest) {
		// 이메일 중복 검증
		String email = memberRequest.getEmail();
		boolean existsEmail = memberRepository.existsByEmail(email);
		
		// 이메일 중복 시 ErrorMessage 응답
		if(existsEmail) return GlobalResponse.error(ErrorMessage.DUPLI_EMAIL);
		
		// 닉네임 중복 검증
		String nickname = memberRequest.getNickname();
		boolean existsNickname = memberRepository.existsByNickname(nickname);
		
		// 닉네임 중복 시 ErrorMessage 응답
		if(existsNickname) return GlobalResponse.error(ErrorMessage.DUPLI_NICKNAME);
		
		// 데이터베이스에 저장할 패스워드 인코딩
		String encodedPassword = passwordEncoder.encode(memberRequest.getPassword());
		
		// 데이터베이스에 저장할 Member Entity 생성
		// 회원가입 시 권한 => MEMBER
		// 회원가입일자     => 현재시간
		Member member = Member.builder()
				.email(email)
				.nickname(nickname)
				.password(encodedPassword)
				.role(Role.MEMBER)
				.signinDate(LocalDateTime.now())
				.build();
		
		// Member Entity 저장
		memberRepository.save(member);
		
		// 성공 응답 리턴
		return GlobalResponse.success("회원가입이 완료되었습니다.");
	}
	
	/*
	 * Seo-JS0823
	 * 
	 * 로그인
	 */
	@Transactional
	public GlobalResponse<?> login(HttpServletRequest request, MemberRequest memberRequest) {
		// 요청 이메일과 패스워드로 일치성 검증
		if(!memberService.loginEmailAndPasswordValidate(memberRequest)) return GlobalResponse.error(ErrorMessage.LOGIN_NOT_MATCH);
		
		// 이메일로 Member Entity 조회
		Member login = memberService.getMember(memberRequest);
		
		// Member Entity 가 가지고 있는 Device List 가져오기
		List<Device> devices = login.getDevices();
		
		// List<Device>가 하나라도 없으면 신규 기기 로그인
		if(devices.size() == 0) return newLogin(request, login, false);
		
		// HttpServletRequest로 쿠키에서 Device-ID 존재 유무 확인
		if(!CookieUtil.containsCookie(CookieName.DEVICE, request)) return newLogin(request, login, false);
		
		// 존재하면 가져오기
		String deviceIdFromCookie = CookieUtil.getCookieValue(CookieName.DEVICE, request);
		
		// 쿠키에서 가져온 DeviceID가 Device Entities 에 일치하는 레코드가 있는지 판단
		Device device = deviceIdSearch(devices, deviceIdFromCookie);
		
		// DeviceId가 없으면 신규 기기 로그인
		if(device == null) return newLogin(request, login, false);
		
		// DeviceId가 일치하는 경우 쿠키에서 RefreshToken 존재 유무 확인
		if(!CookieUtil.containsCookie(CookieName.REFRESH_TOKEN, request)) return newLogin(request, login, true); 
		
		// Device RefreshToken과 Cookie RefreshToken 일치성 확인
		if(!refreshTokenAndDbValid(CookieName.REFRESH_TOKEN, request, device)) return attacker(login, request);
		
		// Token Email과 Member Entity Email이 동일하면 새 RefreshToken 발급
		String newRefreshToken = jwtTokenProvider.createToken(login.getEmail(), TokenType.REFRESH);
		
		// [1] Device Entity에 새로 발급한 RefreshToken으로 업데이트
		device.updateRefreshToken(newRefreshToken);
		
		// AccessToken 발급
		String accessToken = jwtTokenProvider.createToken(login.getEmail(), TokenType.ACCESS);
		
		// Token 전용 객체에 토큰을 넣어서 GlobalResponse.success 응답
		TokenResponse tokenResponse = TokenResponse.builder()
				.accessToken(accessToken)
				.refreshToken(newRefreshToken)
				.build();
		
		return GlobalResponse.successLocation(tokenResponse, "로그인 요청 성공", "/");
	}
	
	private boolean refreshTokenAndDbValid(CookieName cookie, HttpServletRequest request, Device device) {
		String refreshTokenFromCookie = CookieUtil.getCookieValue(cookie, request);
		String refreshTokenFromDevice = device.getRefreshToken();
		
		if(!refreshTokenFromCookie.equals(refreshTokenFromDevice)) return false;
		
		return true;
	}
	
	private Device deviceIdSearch(List<Device> devices, String deviceId) {
		Optional<Device> matchedDevice = devices.stream()
				.filter(device -> device.getDeviceId().equals(deviceId))
				.findFirst();
		
		if(matchedDevice.isEmpty()) return null;
		
		return matchedDevice.get();
	}
	
	private GlobalResponse<?> newLogin(HttpServletRequest request, Member member, boolean isDeviceId) {
		TokenResponse tokenResponse = null;
		
		String refreshToken = jwtTokenProvider.createToken(member.getEmail(), TokenType.REFRESH);
		
		String accessToken = jwtTokenProvider.createToken(member.getEmail(), TokenType.ACCESS);
		
		if(isDeviceId) {
			Device device = deviceRepository.findByDeviceId(CookieUtil.getCookieValue(CookieName.DEVICE, request));
			
			
			device.updateRefreshToken(refreshToken);
			
			tokenResponse = TokenResponse.builder()
					.refreshToken(refreshToken)
					.accessToken(accessToken)
					.build();
			
			return GlobalResponse.success(tokenResponse, "로그인 요청 성공");
		}
		
		String ip = request.getRemoteAddr();
		
		// User-Agent 파싱 문자열
		String deviceName = deviceInfo(request);
		
		String deviceId = UUID.randomUUID().toString();
		
		Device device = Device.builder()
				.deviceId(deviceId)
				.refreshToken(refreshToken)
				.deviceName(deviceName)
				.ip(ip)
				.member(member)
				.build();
		
		deviceRepository.save(device);
		
		tokenResponse = TokenResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.deviceId(deviceId)
				.build();
		
		return GlobalResponse.success(tokenResponse, "로그인 요청 성공");
	}
	
	private String deviceInfo(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		Client c = uaParser.parse(userAgent);
		String model = mappingModel(c.device.family);
		String osName = c.os.family;
		String osMajor = Optional.ofNullable(c.os.major).orElse("?");
		return String.format("%s/%s_%s", model, osName, osMajor);
	}
	
	private String mappingModel(String model) {
		if(model.equals("Other")) return "Desktop";
		return model;
	}
	
	private GlobalResponse<?> attacker(Member Entity, HttpServletRequest request) {
		// 위에서 조회한 Member Entity가 가진 RefreshToken 폐기 처리
		
		// Token에서 추출한 Email을 가진 Member Entity가 가진 RefreshToken 폐기 처리
		
		// 요청자 IP, MemberRequest의 Email, RefreshToken의 Email, 로그인 시간(Timestamp)을 로그파일로 기록
		
		// 사용자 정의 익셉션 발생 --> RefreshTokenAndEmailNotValidException
		
		// 컨트롤러에서는 위 익셉션이 발생하면 요청에 쿠키를 삭제하라는 응답 내려보내고 로그인 재시도
		
		return null;
	}
	
	@Transactional
	public GlobalResponse<?> memberInfoUpdate(MemberRequest memberRequest, HttpServletRequest request) {
		// 요청자의 Authentication 객체에 들어있는 MemberDetails의 Email을 가져온다.
		String contextFromEmail = contextFromEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		
		System.out.println("회원정보 변경 요청자 : " + contextFromEmail);
		
		// 요청자의 쿠키에 들어 있는 RefreshToken을 가져온다.
		String refreshToken = CookieUtil.getCookieValue(CookieName.REFRESH_TOKEN, request);
		
		// contextFromEmail로 요청자의 DB에 있는 Device 객체를 가져온다.
		Member member = memberRepository.findByEmailWithDevices(contextFromEmail);
		List<Device> devices = member.getDevices();
		
		// 요청자의 쿠키에 들어있는 RefreshToken과 Devices Entity의 RefreshToken과 일치하는 것이 있는지 판별
		List<Device> matchedDevice = devices.stream()
		.filter(device -> device.getRefreshToken().equals(refreshToken))
		.toList();
		
		// 일치하지 않으면 에러메시지 응답
		if(matchedDevice.size() == 0) return GlobalResponse.error(ErrorMessage.AUTH_NOT_MATCH);
		
		// memberRequest의 password와 nickname 가져오기
		String updatePassword = memberRequest.getPassword();
		
		String updateNickname = memberRequest.getNickname();
		
		// 기존 패스워드와 사용자 입력받은 패스워드의 일치성 검증
		boolean updatePasswordValid = passwordEncoder.matches(updatePassword, member.getPassword());
		if(!updatePasswordValid) member.updatePassword(passwordEncoder.encode(updatePassword));
		
		// 기존 닉네임과 사용자 입력받은 닉네임의 일치성 검증
		boolean updateNicknameValid = updateNickname.equals(member.getNickname());
		if(!updateNicknameValid) {
			// 닉네임 중복 확인
			boolean existsNickname = memberRepository.existsByNickname(updateNickname);
			if(existsNickname) return GlobalResponse.error(ErrorMessage.DUPLI_NICKNAME);
			
			member.updateNickname(updateNickname);
		}
		
		return GlobalResponse.success("회원정보가 변경되었습니다.");
	}
	
	private String contextFromEmail(Object details) {
		String email = null;
		if(details instanceof MemberDetails) {
			email = ((MemberDetails) details).getUsername();
		}
		return email;
	}
	
	@Transactional
	public GlobalResponse<?> memberDelete(MemberRequest memberRequest) {
		
		return null;
	}
	
}
