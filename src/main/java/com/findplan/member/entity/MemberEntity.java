package com.findplan.member.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.findplan.device.entity.DeviceEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
	@Column(name = "member_seq")
	private Long memberSeq;
	
	@Column(name = "member_nickname", length = 10, unique = true, nullable = false)
	private String memberNickname;
	
	@Column(name = "member_password", length = 300, nullable = false)
	private String memberPassword;
	
	@Column(name = "member_email", length = 320, unique = true, nullable = false)
	private String memberEmail;
	
	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberRole role;
	
	@Column(name = "member_create_at", nullable = false)
	private LocalDateTime createAt;
	
	@Builder.Default
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DeviceEntity> devices = new ArrayList<>();
	
	public void signupPasswordEncode(PasswordEncoder passwordEncoder) {
		this.memberPassword = passwordEncoder.encode(this.memberPassword);
	}
	
}
