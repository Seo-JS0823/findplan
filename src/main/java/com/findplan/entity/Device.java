package com.findplan.entity;

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

@Entity
@Table(name = "DEVICE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_id")
	private Long authId;
	
	@Column(name = "ip", nullable = false, length = 50)
	private String ip;
	
	@Column(name = "device_id", unique = true, nullable = false)
	private String deviceId;
	
	@Column(name = "device_name", nullable = false)
	private String deviceName;
	
	@Column(name = "refresh_token", nullable = true, length = 300)
	private String refreshToken;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "code")
	private Member member;
	
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
