package com.findplan.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	private final SecretKey key;
	
	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}
	
	public String createToken(String username, TokenType tokenType) {
		return jwtsCompact(username, tokenType);
	}
	
	private String jwtsCompact(String username, TokenType tokenType) {
		Date now = new Date();
		Date expireTime = new Date(now.getTime() + tokenType.getExipreTime());
		
		return Jwts.builder()
				.subject(username)
				.issuedAt(now)
				.expiration(expireTime)
				.signWith(key)
				.compact();
	}
	
	public String getEmailFromToken(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	public boolean validateToken(String token) {
		
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
