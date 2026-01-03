package com.findplan.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.findplan.enums.TokenType;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {
	
	private final SecretKey key;
	
	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}
	
	public Map<TokenType, String> createTokenAll(String email) {
		Map<TokenType, String> tokens = new HashMap<>();
		tokens.put(TokenType.ACCESS, createToken(email, TokenType.ACCESS));
		tokens.put(TokenType.REFRESH, createToken(email, TokenType.REFRESH));
		
		return tokens;
	}
	
	public String createToken(String email, TokenType tokenType) {
		return jwtsCompact(email, tokenType);
	}
	
	private String jwtsCompact(String email, TokenType tokenType) {
		Date now = new Date();
		Date expireTime = new Date(now.getTime() + tokenType.getExpireTime());
		
		return Jwts.builder()
				.subject(email)
				.issuedAt(now)
				.expiration(expireTime)
				.signWith(key)
				.compact();
	}
	
	public String getUsernameFromToken(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	/*
	 * Jwt 토큰 검증
	 * 
	 * 1. 서명 검증
	 * 		전달받은 토큰의 Signature 부분을 서버가 가진 Key와 비교함.
	 * 		이때, 일치하지 않으면 SignatureException을 통해 위조된 토큰임을 알린다.
	 * 
	 * 2. 유효 기간 검증
	 * 		토큰의 Payload 에 들어있는 exp (Expire Time)을 검증한다.
	 * 		만료 기간이 지나면 ExpiredJwtException을 던진다.
	 * 
	 * 3. 토큰의 구조와 형식 검증
	 * 		토큰이 xxxxx.yyyyy.zzzzz 인 3단 구조를 갖추고 있는지 확인한다.
	 * 		Base64 디코딩이 정상적으로 되는지 확인한다.
	 * 		문제가 생기면 MalformedJwtException을 던진다.
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.error("Jwt Signature Exception: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("Jwt Token Expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("Jwt Unsupported, 지원하지 않는 JWT 형식");
		}
		return false;
	}
	
}
