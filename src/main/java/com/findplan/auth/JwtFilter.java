package com.findplan.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	private final MemberDetailsService memberDetailsService;
	
	private String getToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		
		if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		
		return null;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = getToken(request);
		
		if(token != null && jwtTokenProvider.validateToken(token)) {
			String email = jwtTokenProvider.getEmailFromToken(token);
			UserDetails principal = memberDetailsService.loadUserByUsername(email);
			
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
			
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		filterChain.doFilter(request, response);
		
	}

}
