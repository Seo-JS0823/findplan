package com.findplan.config.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.findplan.config.security.jwt.JwtTokenProvider;
import com.findplan.service.MemberDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * 2025-12-20
 * JwtFilter JWT 토큰 검증 필터
 * 
 * Seo-JS0823
 */
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	
	private final MemberDetailsService memberDetailsService;
	
	public JwtFilter(
			JwtTokenProvider jwtTokenProvider,
			MemberDetailsService memberDetailsService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.memberDetailsService = memberDetailsService;
	}
	
	private String getJwtFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		
		if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		
		return "";
	}
	
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String token = getJwtFromRequest(request);
		
		if(jwtTokenProvider.validateToken(token)) {
			String username = jwtTokenProvider.getUsernameFromToken(token);
			UserDetails details = memberDetailsService.loadUserByUsername(username);
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				details, null, details.getAuthorities()
			);
			
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		filterChain.doFilter(request, response);
	}

}
