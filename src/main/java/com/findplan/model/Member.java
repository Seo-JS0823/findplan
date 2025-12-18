package com.findplan.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER")
@NoArgsConstructor
@Getter
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "email", length = 30, nullable = false, unique = true)
	private String email;
	
	@Column(name = "password", length = 256, nullable = false)
	private String password;
	
	@Column(name = "nickname", length = 10, nullable = false)
	private String nickname;
	
	@Column(name = "role", length = 10, nullable = false)
	private String role;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime regdate;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Authority> authorities = new ArrayList<>();
	
	@Builder
	public Member(String email, String password, String nickname, String role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
	}
	
	@PrePersist
	public void regdateDefault() {
		this.regdate = LocalDateTime.now();
	}
	
	public void addAuthority(Authority authority) {
		this.authorities.add(authority);
	}
	
}
