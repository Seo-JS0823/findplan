package com.findplan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.findplan.auth.JwtFilter;
import com.findplan.auth.JwtTokenProvider;
import com.findplan.auth.MemberDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	
	private final MemberDetailsService memberDetailsService;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf(AbstractHttpConfigurer::disable)
		.cors(AbstractHttpConfigurer::disable)
		.headers(headers -> headers.frameOptions(frame -> frame.disable()))
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.formLogin(AbstractHttpConfigurer::disable)
		.httpBasic(AbstractHttpConfigurer::disable)
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/**").permitAll()
				.anyRequest().authenticated())
		.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	JwtFilter jwtFilter() {
		return new JwtFilter(jwtTokenProvider, memberDetailsService);
	}
	
}
