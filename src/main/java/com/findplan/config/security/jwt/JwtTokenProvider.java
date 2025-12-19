package com.findplan.config.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/*
 * 2025-12-20
 * JWT Token 생성, 검증 담당 객체
 * 
 * Seo-JS0823
 */
@Component
public class JwtTokenProvider {
	// JWT SecretKey
	private final SecretKey key;
	
	// application.yml -> jwt.secret DI
	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}
	
	public String createToken(Authentication authentication, TokenType tokenType) {
		String username = authentication.getName();
		return jwtsCompact(username, tokenType);
	}
	
	private String jwtsCompact(String username, TokenType tokenType) {
		Date now = new Date();
		Date expireTime = new Date(now.getTime() + tokenType.getExpireTime());
		
		return Jwts.builder()
				.subject(username)
				.issuedAt(now)
				.expiration(expireTime)
				.signWith(key)
				.compact();
	}
	
	// 토큰에서 Member::email 가져오기
	public String getUsernameFromToken(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	public boolean validateToken(String token) {
		if(token.isEmpty()) {
			return false;
		}
		
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      System.out.println("잘못된 JWT 서명입니다. : " + e.getMessage());
	  } catch (ExpiredJwtException e) {
	      System.out.println("만료된 JWT Token: " + e.getMessage());
	  } catch (UnsupportedJwtException e) {
	      System.out.println("지원하지 않는 JWT Token: " + e.getMessage());
	  }
		return false;
	}
	
}
