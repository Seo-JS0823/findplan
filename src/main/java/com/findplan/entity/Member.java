package com.findplan.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long code;
	
	@Column(name = "email", unique = true, length = 50, updatable = false)
	private String email;
	
	@Column(name = "password", length = 300, nullable = false)
	private String password;
	
	@Column(name = "nickname", length = 10, unique = true)
	private String nickname;
	
	@Column(name = "signin_date", nullable = false, updatable = false)
	private LocalDateTime signinDate;
	
	@Column(name = "role", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private Role role;
	
}
