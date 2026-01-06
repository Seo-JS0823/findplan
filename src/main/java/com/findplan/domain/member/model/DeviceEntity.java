package com.findplan.domain.member.model;

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
	
	@Column(name = "D_REFRESH_TOKEN", nullable = false)
	private String refreshToken;
	
	@Column(name = "D_DEVICE", nullable = false)
	private String device;
	
	@Column(name = "D_OS", nullable = false)
	private String os;
	
	@Column(name = "D_BROWSER", nullable = false)
	private String browser;
	
	@Column(name = "D_IP", nullable = false)
	private String ip;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_ids")
	private MemberEntity member;
	
	public void setMember(MemberEntity member) {
		this.member = member;
	}
	
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
