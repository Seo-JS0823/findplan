package com.findplan.security.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.findplan.enums.SecurityEnumerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		
		SecurityEnumerator exception = (SecurityEnumerator) request.getAttribute("exception");
		
		if(exception != null) {
			if(SecurityEnumerator.EXPIRED_TOKEN.equals(exception)) {
				setResponse(response, exception.getException(), HttpServletResponse.SC_UNAUTHORIZED);
			}
		} else {
			exception = SecurityEnumerator.UNAUTHO;
		}
		
		setResponse(response, exception.getException(), HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	private void setResponse(HttpServletResponse response, String message, int status) throws IOException{
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(status);
		response.getWriter().println("{\"message\" : \"" + message + "\"}");
	}
	
}
