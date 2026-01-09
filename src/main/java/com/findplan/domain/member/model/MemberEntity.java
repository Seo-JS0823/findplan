package com.findplan.domain.member.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "M_IDS")
	private Long id;
	
	@Column(name = "M_EMAIL", unique = true, nullable = false, length = 30)
	private String email;
	
	@Column(name = "M_PASSWORD", nullable = false, length = 300)
	private String password;
	
	@Column(name = "M_NICKNAME", unique = true, nullable = false, length = 10)
	private String nickname;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "M_ROLE", nullable = false)
	private MemberRole roles;
	
	@Column(name = "M_CREATED_AT", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "M_DELETED", nullable = false)
	private boolean deleted;
	
	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DeviceEntity> devices = new ArrayList<>();
	
	public void addDevice(DeviceEntity device) {
		devices.add(device);
		device.setMember(this);
	}
	
	public void updatePassword(PasswordEncoder encoder, String newPassword) {
		this.password = encoder.encode(newPassword);
	}
	
	public void withdraw() {
		this.deleted = true;
	}
	
}
