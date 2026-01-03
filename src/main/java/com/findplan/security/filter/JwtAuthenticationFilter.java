package com.findplan.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.findplan.enums.SecurityEnumerator;
import com.findplan.security.CustomDetailsService;
import com.findplan.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	
	private final CustomDetailsService detailsService;
	
	private String getJwtFromRequest(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");
		
		if(StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
			return accessToken.substring(7);
		}
		
		return null;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String accessToken = getJwtFromRequest(request);
		
		try {
			if(accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
				String username = jwtTokenProvider.getUsernameFromToken(accessToken);
				UserDetails details = detailsService.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					details, null, details.getAuthorities()
				);
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (ExpiredJwtException e) {
			request.setAttribute("exception", SecurityEnumerator.EXPIRED_TOKEN);
		} finally {
			filterChain.doFilter(request, response);
		}
	}
	
}
