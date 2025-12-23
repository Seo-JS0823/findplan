package com.findplan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.auth.JwtTokenProvider;
import com.findplan.auth.TokenType;
import com.findplan.entity.Device;
import com.findplan.entity.Member;
import com.findplan.entity.Role;
import com.findplan.repository.MemberRepository;
import com.findplan.transfer.ErrorMessage;
import com.findplan.transfer.GlobalResponse;
import com.findplan.transfer.request.MemberRequest;
import com.findplan.transfer.response.TokenResponse;
import com.findplan.utility.CookieName;
import com.findplan.utility.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ua_parser.Parser;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final MemberRepository memberRepository;
	
	private final JwtTokenProvider jwtTokenProvider;
	
	private final PasswordEncoder passwordEncoder;
	
	private final Parser parser;
	
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
	public GlobalResponse<?> login(HttpServletRequest request, MemberRequest memberRequest) {		
		// 이메일로 Member Entity 조회
		String emailFromRequest = memberRequest.getEmail();
		Member login = memberRepository.findByEmailWithDevices(emailFromRequest);
		
		// 존재하지 않는 이메일이면 ErrorResponse 응답
		boolean existsLoginEmail = memberRepository.existsByEmail(emailFromRequest);
		if(!existsLoginEmail) return GlobalResponse.error(ErrorMessage.LOGIN_EMAIL_NOT_MATCH);
		
		// 조회한 엔티티에서 가져온 EncodedPassword와 MemberRequest의 Password 비교
		String passwordFromRequest = memberRequest.getPassword();
		String passwordFromEntity = login.getPassword();
		boolean existsPassword = passwordEncoder.matches(passwordFromRequest, passwordFromEntity);
		
		// 패스워드가 일치하지 않으면 ErrorResponse 응답
		if(!existsPassword) return GlobalResponse.error(ErrorMessage.LOGIN_PASSWORD_NOT_MATCH);
		
		// Member Entity 가 가지고 있는 Device List 가져오기
		List<Device> devices = login.getDevices();
		
		// List<Device>가 하나라도 없으면 신규 기기 로그인 메서드로 분기
		if(devices.size() == 0) return newLogin(login);
		
		// HttpServletRequest로 쿠키에서 Device-ID 가져오기
		String deviceIdFromCookie = CookieUtil.getCookieValue(CookieName.DEVICE, request);
		
		// 쿠키에서 가져온 Device-ID가 null이면 신규 기기 로그인 메서드로 분기
		if(deviceIdFromCookie == null) return newLogin(login);
		
		// 쿠키에서 가져온 DeviceID가 Device Entities 에 일치하는 레코드가 있는지 판단
		Optional<Device> matchedDevice = devices.stream()
				.filter(device -> device.getDeviceId().equals(deviceIdFromCookie))
				.findFirst();
		
		// DeviceId가 일치하지 않으면 신규 기기 로그인 메서드로 분기
		if(matchedDevice.isEmpty()) return newLogin(login);
		
		// [1] 일치하는 Device Entity 1개 따로 변수에 저장
		Device device = matchedDevice.get();
		
		// DeviceId가 일치하는 경우 쿠키에서 RefreshToken 가져오기
		String refreshTokenFromCookie = CookieUtil.getCookieValue(CookieName.REFRESH_TOKEN, request);

		// 해당 RefreshToken과 DB에 저장된 RefreshToken이 일치한지 검사
		boolean refreshTokenEquals = false;
		
		if(device.getRefreshToken().equals(refreshTokenFromCookie)) refreshTokenEquals = true;
		
		// RefreshToken이 일치하지 않으면 해당 RefreshToken에서 Email 추출
		if(!refreshTokenEquals) {
			String emailFromRefreshToken = jwtTokenProvider.getEmailFromToken(refreshTokenFromCookie);
			
			// Member Entity의 Email과 Token에서 추출한 Email이 동일하지 않을 때 실행
			if(!login.getEmail().equals(emailFromRefreshToken)) return attacker(login);
		}
		
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
		
		return GlobalResponse.successLocation(tokenResponse, null, "/");
	}
	
	private GlobalResponse<?> newLogin(Member member) {
		// UserAgentParser 이용해서 HttpServletRequest 에서 User-Agent 정보 파싱
		
		// 파싱한 내용 deviceName 변수에 저장
		
		// Device Entity 객체 생성
		
		// Device Entity에 Member Entity 주입
		
		// DeviceRepository로 Device Entity 저장
		
		String deviceId = UUID.randomUUID().toString();
		String accessToken = jwtTokenProvider.createToken(member.getEmail(), TokenType.ACCESS);
		String refreshToken = jwtTokenProvider.createToken(member.getEmail(), TokenType.REFRESH);
		
		TokenResponse tokenResponse = TokenResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.deviceId(deviceId)
				.build();
		
		return GlobalResponse.success(tokenResponse);
	}
	
	private GlobalResponse<?> attacker(Member Entity) {
		// 위에서 조회한 Member Entity가 가진 RefreshToken 폐기 처리
		// Token에서 추출한 Email을 가진 Member Entity가 가진 RefreshToken 폐기 처리
		// 요청자 IP, MemberRequest의 Email, RefreshToken의 Email, 로그인 시간(Timestamp)을 로그파일로 기록
		// 사용자 정의 익셉션 발생 --> RefreshTokenAndEmailNotValidException
		// 컨트롤러에서는 위 익셉션이 발생하면 요청에 쿠키를 삭제하라는 응답 내려보내고 로그인 재시도
		return null;
	}
	
	
}
