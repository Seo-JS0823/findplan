package com.findplan.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.findplan.auth.JwtTokenProvider;
import com.findplan.auth.TokenType;
import com.findplan.entity.Member;
import com.findplan.repository.MemberRepository;
import com.findplan.transfer.request.MemberRequest;
import com.findplan.transfer.response.AuthResponse;
import com.findplan.transfer.response.ErrorCode;
import com.findplan.utility.CookieName;
import com.findplan.utility.CookieUtil;
import com.findplan.utility.UserAgentParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	
	private final MemberDeviceService memberDeviceService;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtTokenProvider jwtTokenProvider;
	
	public Map<String, String> signin(MemberRequest request) {
		boolean dupliEmail = memberRepository.existsByEmail(request.getEmail());
		boolean dupliNickname = memberRepository.existsByNickname(request.getNickname());
		boolean deletedMember = memberRepository.existMember(request.getEmail());
		
		if(dupliEmail) return ErrorCode.DUPLI_EMAIL.getResponse();
		if(dupliNickname) return ErrorCode.DUPLI_NICKNAME.getResponse();
		if(deletedMember) return ErrorCode.DELETE_MEMBER.getResponse();
		
		Member member = request.toSigninEntity(passwordEncoder);
		
		memberRepository.save(member);
		
		return new AuthResponse("/home").getResponse();
	}
	
	/*
	 * 2025-12-21	
	 * LoginFilter 에 있던 로직을 MemberService로 옮겨서 다시 구현.
	 * 
	 * Seo-JS0823
	 */
	public Map<String, String> login(HttpServletRequest request, MemberRequest memberRequest) {
		// 헤더에서 필요한 정보 추출 후 주입
		memberRequest.setUserAgent(request.getHeader("User-Agent"));
		memberRequest.setIp(request.getRemoteAddr());
		
		// 이미 탈퇴 처리된 이메일인지 검증, 실패시 에러코드 응답
		boolean deletedMember = memberRepository.existMember(memberRequest.getEmail());
		if(deletedMember) return ErrorCode.DELETE_MEMBER.getResponse();
		
		// 아이디, 패스워드 검증, 실패시 에러코드 응답
		Member loginMember = memberRepository.findByEmail(memberRequest.getEmail()).orElse(null);
		if(loginMember == null) return ErrorCode.NOT_FOUND_EMAIL.getResponse();
		
		// DB에 저장된 비밀번호
		String oldPassword = loginMember.getPassword();
		
		// PasswordEncoder로 비밀번호 검증, 실패시 에러코드 응답
		boolean passwordMatch = passwordEncoder.matches(memberRequest.getPassword(), oldPassword);
		if(!passwordMatch) return ErrorCode.PASSWORD_NOT_MATCH.getResponse();
		
		// 쿠키에서 Device-ID 추출하고 새 기기에서 로그인 한 것인지 판단
		String deviceId = CookieUtil.getCookieValue(request, CookieName.DEVICE);
		boolean loginDeviceExists = memberDeviceService.loginDeviceExists(loginMember.getCode(), deviceId);
		
		// 새로운 기기에서 로그인 했을 시 newLogin 메소드 호출
		if(loginDeviceExists) return newLogin(memberRequest, loginMember);
		
		// 기존 기기에서 로그인 했을 시 oldLogin 메소드 호출
		else {
			String refreshToken = CookieUtil.getCookieValue(request, CookieName.REFRESH_TOKEN);
			return oldLogin(refreshToken, loginMember, deviceId);
		}
	}
	
	private Map<String, String> newLogin(MemberRequest memberRequest, Member loginMember) {
		// MemberRequest에서 userAgent 정보를 꺼내서 DB에 저장할 Device 정보 추출하고 MemberRequest에 주입
		Map<String, String> deviceInfo = UserAgentParser.getDeviceInfo(memberRequest.getUserAgent());
		
		memberRequest.setDevice(deviceInfo);
		
		// AccessToken과 RefreshToken 생성하고 MemberRequest에 주입
		String accessToken = jwtTokenProvider.createToken(memberRequest.getEmail(), TokenType.ACCESS);
		String refreshToken = jwtTokenProvider.createToken(memberRequest.getEmail(), TokenType.REFRESH);
		
		memberRequest.setAccessToken(accessToken);
		memberRequest.setRefreshToken(refreshToken);
		
		// 로그인 기기에 대한 정보 저장 후 deviceId 추출
		String insertedDeviceId = 
			Optional.ofNullable(memberDeviceService.deviceSave(memberRequest, loginMember))
				.orElseThrow(() -> new RuntimeException());
		
		return AuthResponse.builder()
				.device(insertedDeviceId)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.location("/home")
				.build().getResponse();
	}
	
	private Map<String, String> oldLogin(String refreshToken, Member loginMember, String deviceId) {
		// 리프레시 토큰 검증
		boolean refreshTokenExists = jwtTokenProvider.validateToken(refreshToken);
		
		// 리프레시 토큰이 만료되지 않아도 로그인 요청 시 AccessToken 발급
		String accessToken = jwtTokenProvider.createToken(loginMember.getEmail(), TokenType.ACCESS);
		
		// 리프레시 토큰이 만료되었을 경우 새로 발급
		if(!refreshTokenExists) {
			String newRefreshToken = jwtTokenProvider.createToken(loginMember.getEmail(), TokenType.REFRESH);
			// 새로 발급한 리프레시 토큰으로 저장
			memberDeviceService.refreshTokenUpdate(loginMember, newRefreshToken, deviceId);
			
			return AuthResponse.builder()
					.location("/home")
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build().getResponse();
		}
		
		return AuthResponse.builder()
				.location("/home")
				.accessToken(accessToken)
				.build().getResponse();
	}
	
	public Map<String, String> memberUpdate(MemberRequest memberRequest) {
		Member member = memberRepository.findByEmail(memberRequest.getEmail()).orElse(null);
		if(member == null) return ErrorCode.NOT_FOUND_EMAIL.getResponse();
		
		// 요청 데이터에 비밀번호가 다르면 비밀번호 변경 로직
		String password = memberRequest.getPassword();
		boolean updatedPassword = passwordEncoder.matches(password, member.getPassword());
		if(!updatedPassword) member.updatePassword(passwordEncoder.encode(password));
		
		// 요청 데이터에 닉네임이 다르면 닉네임 변경 로직
		String nickname = memberRequest.getNickname();
		boolean updatedNickname = nickname.equals(member.getNickname());
		if(!updatedNickname) {
			member.updateNickname(nickname);
		}
		
		return Map.of("message", "회원 정보가 변경되었습니다.");
	}
	
	public Map<String, String> memberDelete(MemberRequest memberRequest) {
		Member member = memberRepository.findByEmail(memberRequest.getEmail()).orElse(null);
		if(member == null) return ErrorCode.NOT_FOUND_EMAIL.getResponse();
		
		// 멤버 삭제
		memberRepository.delete(member);
		
		return Map.of("delete", "true");
	}
	
}
