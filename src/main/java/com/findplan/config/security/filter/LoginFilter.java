package com.findplan.config.security.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findplan.config.security.jwt.JwtTokenProvider;
import com.findplan.config.security.jwt.TokenType;
import com.findplan.service.MemberDetailsService;
import com.findplan.transfer.request.LoginRequest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * 2025-12-20
 * LoginFilter : 로그인 요청 담당 필터
 * 
 * Seo-JS0823
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	
	private final JwtTokenProvider jwtTokenProvider;
	
	private final MemberDetailsService memberDetailsService;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public LoginFilter(
			AuthenticationManager authenticationManager,
			JwtTokenProvider jwtTokenProvider,
			MemberDetailsService memberDetailsService) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.memberDetailsService = memberDetailsService;
		
		setFilterProcessesUrl("/login");
	}
	
	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		try {
			LoginRequest loginDto = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
			
			System.out.println(loginDto);
			
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginDto.getEmail(),
				loginDto.getPassword()
			);
			
			return authenticationManager.authenticate(authenticationToken);
		} catch (IOException e) {
			throw new RuntimeException("JSON Parsing Error ,", e);
		}
	}
	
	@Override
	protected void successfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authResult) throws IOException {
		
		String accessToken = jwtTokenProvider.createToken(authResult, TokenType.ACCESS);
		String refreshToken = jwtTokenProvider.createToken(authResult, TokenType.REFRESH);
		String deviceInfo = request.getHeader("User-Agent");
		String username = authResult.getName();
		String ip = request.getRemoteAddr();
		memberDetailsService.saveAuthority(username, refreshToken, deviceInfo, ip);
		
		ResponseCookie accessCookie = ResponseCookie.from("find_refresh_token", refreshToken)
				.httpOnly(true)
				.secure(true)
				.sameSite("strict")
				.path("/")
				.maxAge(60 * 30)
				.build();
		
		ResponseCookie refreshCookie = ResponseCookie.from("find_access_token", accessToken)
				.httpOnly(true)
				.secure(true)
				.sameSite("strict")
				.path("/")
				.maxAge(60 * 60 * 24 * 7)
				.build();
		
		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
		
		objectMapper.writeValue(response.getWriter(), Map.of(
			"location", "/home"
		));
	}
}
