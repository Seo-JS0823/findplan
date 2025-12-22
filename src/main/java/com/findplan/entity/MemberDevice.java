package com.findplan.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER_DEVICE")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MemberDevice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_code")
	private Long authId;
	
	@Column(name = "ip", nullable = false)
	private String ip;
	
	@Column(name = "device_id", nullable = false)
	private String deviceId;
	
	@Column(name = "model", nullable = false)
	private String model;
	
	@Column(name = "os", nullable = false)
	private String os;
	
	@Column(name = "browser", nullable = false)
	private String browser;
	
	@Column(name = "refresh_token", nullable = false)
	private String refreshToken;
	
	@Column(name = "last_login_at", nullable = false)
	private LocalDateTime lastLoginAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "code")
	private Member member;
	
	public void loginAtUpdate() {
		this.lastLoginAt = LocalDateTime.now();
	}
	
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
