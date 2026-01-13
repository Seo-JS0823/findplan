package com.findplan.global.config;

import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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

import com.findplan.domain.member.model.MemberRole;
import com.findplan.global.auth.JwtTokenProvider;
import com.findplan.global.auth.MemberDetailsService;
import com.findplan.global.auth.filter.CsrfCookieFilter;
import com.findplan.global.auth.filter.JwtAuthenticationFilter;
import com.findplan.global.util.CookieProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	
	private final MemberDetailsService memberDetailsService;
	
	private final CookieProvider cookieProvider;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
			.cors(cors -> cors.disable())
			
			.csrf(csrf -> csrf
					.ignoringRequestMatchers("/h2-console/**")
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
					.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
			
			.headers(headers -> headers
					.frameOptions(frame -> frame.sameOrigin()))
			
			.formLogin(login -> login.disable())
			
			.httpBasic(basic -> basic.disable())
			
			.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
			
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			
			.authorizeHttpRequests(auth -> auth
					// 정적 리소스
					.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
					// 로그인, 회원가입
					.requestMatchers("/", "/api/member/login", "/api/member/signup", "/h2-console/**", "/favicon.ico", "/error").permitAll()
					// 회원가입 시 중복 체크
					.requestMatchers("/api/member/dupli-e", "/api/member/dupli-n", "/api/member/me", "/api/member/logout").permitAll()
					
					.requestMatchers("/api/member/**", "/main").hasAnyAuthority(MemberRole.MEMBER.getRoles().toArray(new String[0]))
					.anyRequest().authenticated());
		
		return http.build();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("http:/localhost:8080");
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setMaxAge(3600L);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		return source;
	}
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
				.requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/error", "/.well-known/**");
	}
	
	@Bean
	JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, memberDetailsService, cookieProvider);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	CsrfCookieFilter csrfCookieFilter() {
		return new CsrfCookieFilter();
	}
	
}
