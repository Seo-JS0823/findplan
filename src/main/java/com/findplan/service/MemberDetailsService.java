package com.findplan.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.findplan.model.Authority;
import com.findplan.model.Member;
import com.findplan.model.MemberDetails;
import com.findplan.repository.AuthorityRepository;
import com.findplan.repository.MemberRepository;
import com.findplan.service.exception.AuthorityExceptionMessage;

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

	public void saveAuthority(String username, String refreshToken, String deviceInfo) {
		Member member = memberRepository.findByEmail(username);
		
		Client c = uaParser.parse(deviceInfo);
		
		String model = mappingModel(c.device.family);
		String os = c.os.family;
		String osMajor = Optional.ofNullable(c.os.major).orElse("?");
		String browser = c.userAgent.family;
		String browserMajor = Optional.ofNullable(c.userAgent.major).orElse("?");
		
		String device = String.format("%s/%s_%s/%s_%s",
			model, os, osMajor, browser, browserMajor
		);
		
		Authority authority = Authority.builder()
				.deviceInfo(device)
				.refreshToken(refreshToken)
				.member(member)
				.build();
		
		Authority inserted = authorityRepository.save(authority);
		
		if(inserted == null) {
			throw AuthorityExceptionMessage.NOT_SAVED.exception();
		}
	}
	
	private String mappingModel(String deviceModel) {
		if(deviceModel.equals("Other")) {
			return "Desktop";
		}
		return deviceModel;
	}

}
