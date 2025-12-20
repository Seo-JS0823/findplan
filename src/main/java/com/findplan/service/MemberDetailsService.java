package com.findplan.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.findplan.model.Authority;
import com.findplan.model.Member;
import com.findplan.model.MemberDetails;
import com.findplan.repository.AuthorityRepository;
import com.findplan.repository.MemberRepository;
import com.findplan.transfer.AuthorityDto;

import jakarta.transaction.Transactional;
import ua_parser.Client;
import ua_parser.Parser;

/*
 * 2025-12-20
 * Security 회원 인증/인가 서비스
 * 
 * Seo-JS0823
 */
@Service
public class MemberDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;
	
	private final AuthorityRepository authorityRepository;
	
	private final Parser uaParser;
	
	public MemberDetailsService(
			MemberRepository memberRepository,
			Parser uaParser,
			AuthorityRepository authorityRepository) {
		this.memberRepository = memberRepository;
		this.uaParser = uaParser;
		this.authorityRepository = authorityRepository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(email);
		
		if(member == null) {
			throw new UsernameNotFoundException("존재하지 않는 유저입니다.");
		}
		
		return new MemberDetails(member);
	}

	public String saveAuthority(String username, String refreshToken, AuthorityDto authorityDto) {
		// username(email)으로 Member Entity 조회
		Member member = memberRepository.findByEmail(username);
		
		// 조회한 Member Entity의 Id로 Authority List 조회
		List<Authority> authority = authorityRepository.findByMemberCode(member.getCode());
		
		// Authority List에서 Device-Id 정보와 일치하는 레코드가 있는지 검색
		// 일치하면 Save 할 필요 없으니 메소드 종료
		for(Authority auth : authority) {
			String deviceId = authorityDto.getDeviceId();
			if(deviceId.equals(auth.getDeviceId())) {
				return "";
			}
		}
		
		// 일치하는 레코드가 없으면 Authority Entity 객체를 만들어서 Save
		String parsedDevice = insertedDeviceInfo(authorityDto.getDeviceInfo());
		Authority target = insertedAuthority(authorityDto, parsedDevice, member);
		
		Authority inserted =  authorityRepository.save(target);
		
		return inserted.getDeviceId();
	}
	
	private Authority insertedAuthority(AuthorityDto authorityDto, String parsedDevice, Member member) {
		return Authority.builder()
				.deviceId(UUID.randomUUID().toString())
				.deviceInfo(parsedDevice)
				.refreshToken(authorityDto.getRefreshToken())
				.ip(authorityDto.getIp())
				.member(member)
				.build();
	}
	
	private String insertedDeviceInfo(String deviceInfo) {
		Client c = uaParser.parse(deviceInfo);
		
		String model = mappingModel(c.device.family);
		String osName = c.os.family;
		String osMajor = Optional.ofNullable(c.os.major).orElse("?");
		String browser = c.userAgent.family;
		String browserMajor = Optional.ofNullable(c.userAgent.major).orElse("?");
		
		return String.format("%s/%s_%s/%s_%s",
				model,
				osName,
				osMajor,
				browser,
				browserMajor);
	}
	
	private String mappingModel(String deviceModel) {
		if(deviceModel.equals("Other")) {
			return "Desktop";
		}
		return deviceModel;
	}

}
