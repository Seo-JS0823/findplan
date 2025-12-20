package com.findplan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.findplan.config.security.filter.JwtFilter;
import com.findplan.config.security.filter.LoginFilter;
import com.findplan.config.security.jwt.JwtTokenProvider;
import com.findplan.service.MemberDetailsService;

/*
 * 2025-12-20
 * Security 설정 정보
 * 
 * Seo-JS0823
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	
	private final MemberDetailsService memberDetailsService;
	
	private final AuthenticationConfiguration authenticationConfiguration;
	
	public SecurityConfig(
			JwtTokenProvider jwtTokenProvider, 
			MemberDetailsService memberDetailsService,
			AuthenticationConfiguration authenticationConfiguration) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.memberDetailsService = memberDetailsService;
		this.authenticationConfiguration = authenticationConfiguration;
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		AuthenticationManager authenticationManager = authenticationManager(authenticationConfiguration);
		
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
		.addFilterAt(
			new LoginFilter(authenticationManager, jwtTokenProvider, memberDetailsService),
			UsernamePasswordAuthenticationFilter.class
		)
		.addFilterBefore(
			jwtFilter(),
			UsernamePasswordAuthenticationFilter.class
		);
		
		
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	JwtFilter jwtFilter() {
		return new JwtFilter(jwtTokenProvider, memberDetailsService);
	}
	
}
