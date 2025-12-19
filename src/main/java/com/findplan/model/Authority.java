package com.findplan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 2025-12-20
 * Authority Entity
 * 인증/인가 도메인
 * 
 * Member : Authority = 1 : N
 * Seo-JS0823
 */
@Entity
@Table(name = "AUTHORITY")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Authority {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// AUTO-INCREMENT 대리키
	private Long id;
	
	@Column(name = "refresh_token", length = 500, nullable = true)
	// 리프레시 토큰 정보
	private String refreshToken;
	
	@Column(name = "device_info", length = 150, nullable = false)
	// "User-Agent" 파싱후 결과값인 디바이스 정보
	private String deviceInfo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "code")
	private Member member;
	
}
