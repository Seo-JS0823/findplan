package com.findplan.domain.member.model;

import java.time.LocalDateTime;

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
public class DeviceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "D_IDS")
	private Long id;
	
	@Column(name = "D_ID", unique = true, nullable = false, updatable = false)
	private String deviceId;
	
	@Column(name = "D_RT", nullable = false, unique = true)
	private String refreshToken;
	
	@Column(name = "D_DEVICE", nullable = false)
	private String device;
	
	@Column(name = "D_OS", nullable = false)
	private String os;
	
	@Column(name = "D_BROWSER", nullable = false)
	private String browser;
	
	@Column(name = "D_IP", nullable = false)
	private String ip;
	
	@Column(name = "D_DELETED")
	private boolean deleted;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "M_IDS")
	private MemberEntity member;
	
	public void setMember(MemberEntity member) {
		this.member = member;
	}
	
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public void updateOs(String os) {
		this.os = os;
	}
	
	public void updateDevice(String device) {
		this.device = device;
	}
	
	public void updateBrowser(String browser) {
		this.browser = browser;
	}
	
	public void delete() {
		this.deleted = true;
	}
	
	public LoginHistoryEntity createLoginHistory() {
		return LoginHistoryEntity.builder()
				.device(this)
				.member(member)
				.loginAt(LocalDateTime.now())
				.build();
	}
	
}
