package com.findplan.device.entity;

import com.findplan.member.entity.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	@Column(name = "device_seq")
	private Long deviceSeq;
	
	@Column(name = "device_id", nullable = false, unique = true)
	private String deviceId;
	
	@Column(name = "device_name", nullable = false)
	private String deviceName;
	
	@Column(name = "refresh_token", nullable = true)
	private String refreshToken;
	
	@ManyToOne
	@JoinColumn(name = "member_seq")
	private MemberEntity member;
	
	public void refreshTokenUpdate(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
