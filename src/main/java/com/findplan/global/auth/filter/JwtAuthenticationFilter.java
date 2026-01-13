package com.findplan.global.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.findplan.global.auth.JwtTokenProvider;
import com.findplan.global.auth.MemberDetailsService;
import com.findplan.global.util.CookieName;
import com.findplan.global.util.CookieProvider;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	
	private final MemberDetailsService memberDetailsService;
	
	private final CookieProvider cookieProvider;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = getJwtFromRequest(request);
		
		log.info("doFilterInternal : {}", token);
		
		if(token == null) {
			token = cookieProvider.getCookieValue(CookieName.ACCESS, request);
			System.out.println("쿠키에서 가져온 AccessToken : " + token);
		}
		
		log.info("1. 필터 진입 - 토큰 존재 여부: {}", (token != null));
		
		if(token == null || token.isBlank() || !isJwtFormat(token)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			if(jwtTokenProvider.validateToken(token)) {
				log.info("2. 토큰 검증 성공");
				String email = jwtTokenProvider.getEmailFromToken(token);
				UserDetails details = memberDetailsService.loadUserByUsername(email);
				
				if(details == null) {
					log.info("3. loadUserByUsername이 Null 반환");
					cookieProvider.clearSecurityCookie(response);
					filterChain.doFilter(request, response);
					return;
				}
				
				log.info("4. DB에서 사용자 검색 성공");
				
				UsernamePasswordAuthenticationToken authentication = authenticationToken(details);
				
				// buildDetails --> WebAuthenticationDetails.class
				// 요청 부가 정보 저장
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (ExpiredJwtException e) {
			log.warn("만료된 토큰을 이용한 요청입니다. : {}", e.getMessage());
		}
		
		filterChain.doFilter(request, response);
	}
	
	private boolean isJwtFormat(String token) {
		boolean format = token.chars().filter(ch -> ch == '.').count() == 2;
		log.info("1-1. isJwtFormat이 걸린건가?: {}", format);
		log.info("1-2. 무슨 값이 들어왔길래?: {}", token);
		return format;
	}
	
	private UsernamePasswordAuthenticationToken authenticationToken(UserDetails details) {
		return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
	}
	
	private String getJwtFromRequest(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");
		
		if(accessToken != null && accessToken.length() >= 12) {
			return accessToken.substring(7);
		}
		
		return null;
	}
	
}