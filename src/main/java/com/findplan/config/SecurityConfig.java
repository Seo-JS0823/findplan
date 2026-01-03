package com.findplan.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.findplan.member.entity.MemberRole;
import com.findplan.security.CustomDetailsService;
import com.findplan.security.filter.CsrfCookieFilter;
import com.findplan.security.filter.JwtAuthenticationFilter;
import com.findplan.security.jwt.JwtAuthenticationEntryPoint;
import com.findplan.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtTokenProvider jwtTokenProvider;
	
	private final CustomDetailsService detailsService;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		// 세션 미사용
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
		.csrf(csrf -> csrf
			// 테스트 데이터베이스 경로 패스
			.ignoringRequestMatchers("/h2-console/**")
			// CSRF 토큰은 JS에서 접근 가능
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
		// Iframe 허용
		.headers(headers -> headers
			.frameOptions(frame -> frame.sameOrigin())
    )
		.formLogin(formLogin -> formLogin.disable())
		.exceptionHandling(exception -> exception
			.authenticationEntryPoint(jwtAuthenticationEntryPoint())
		)
		.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
		.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
		.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					// 기본 경로 패스
					"/js/**", "/css/**", "/img/**", "/error", "/static/**"
					, "/h2-console/**", "/favicon.ico", "/",
					
					// 회원가입, 로그인 요청
					"/api/member/login", "/api/member/signup",
					
					// 클라이언트의 로그인 상태 확인
					"/api/member/login/state",
					
					// 로그아웃
					"/api/member/logout",
					
					// 회원가입 시 중복체크 URL
					"/api/member/dupli/nickname",
					"/api/member/dupli/email"
				).permitAll()
				.requestMatchers(
					"/api/member"
				).hasAnyAuthority(MemberRole.MEMBER.getRoles().toArray(new String[0]))
				.anyRequest().authenticated())
		;
		
		return http.build();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		
		configuration.setAllowedOrigins(List.of("http://localhost:8080"));
		
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		
		configuration.setAllowedHeaders(List.of("*"));
		
		configuration.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
	}
	
	@Bean
	JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
		return new JwtAuthenticationEntryPoint();
	}
	
	@Bean
	JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, detailsService);
	}
	
	@Bean
	CsrfCookieFilter csrfCookieFilter() {
		return new CsrfCookieFilter();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
