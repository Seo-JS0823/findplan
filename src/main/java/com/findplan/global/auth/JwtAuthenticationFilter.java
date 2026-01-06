package com.findplan.global.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

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
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = getJwtFromRequest(request);
		
		try {
			if(token != null && jwtTokenProvider.validateToken(token)) {
				String email = jwtTokenProvider.getEmailFromToken(token);
				UserDetails details = memberDetailsService.loadUserByUsername(email);
				
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
	
	private UsernamePasswordAuthenticationToken authenticationToken(UserDetails details) {
		return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
	}
	
	private String getJwtFromRequest(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");
		
		if(accessToken != null) {
			return accessToken.substring(7);
		}
		
		return null;
	}
	
}