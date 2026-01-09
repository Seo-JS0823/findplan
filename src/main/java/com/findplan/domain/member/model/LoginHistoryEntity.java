package com.findplan.domain.member.model;

import java.time.LocalDateTime;

import com.findplan.domain.member.transfer.response.LoginHistoryResponse;

import jakarta.persistence.CascadeType;
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
@Table(name = "LOGIN_HISTORY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "L_IDS")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "M_IDS", nullable = true)
	private MemberEntity member;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "D_IDS", nullable = true)
	private DeviceEntity device;
	
	@Column(name = "L_LOGIN_AT", nullable = false)
	private LocalDateTime loginAt;
	
	public LoginHistoryResponse toResponse() {
		return LoginHistoryResponse.builder()
				.ip(device.getIp())
				.device(device.getDevice())
				.browser(device.getBrowser())
				.os(device.getOs())
				.loginTime(LocalDateTime.now())
				.build();
	}
	
}
