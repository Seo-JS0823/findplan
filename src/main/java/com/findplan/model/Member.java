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

/*
 * 2025-12-20
 * Member Entity
 * 회원 도메인
 * 
 * Member : Autority = 1 : N
 * 
 * Seo-JS0823
 */
@Entity
@Table(name = "MEMBER")
@NoArgsConstructor
@Getter
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// AUTO-INCREMENT 대리키
	private Long code;
	
	@Column(name = "email", length = 30, nullable = false, unique = true)
	// Email 정보이며 아이디로 사용
	private String email;
	
	@Column(name = "password", length = 256, nullable = false)
	// 패스워드 BCrypt 암호화 후 저장
	private String password;
	
	@Column(name = "nickname", length = 10, nullable = false)
	// 사용자 닉네임
	private String nickname;
	
	@Column(name = "role", length = 10, nullable = false)
	// 사용자가 가진 권한
	private String role;
	
	@Column(nullable = false, updatable = false)
	// 가입 날짜이며 최초 Insert 이후 수정 불가
	private LocalDateTime regdate;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	// 권한 정보를 가진 Entity
	private List<Authority> authorities = new ArrayList<>();
	
	@Builder
	public Member(String email, String password, String nickname, String role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
	}
	
	// 생성자로 주입하지 않아도 자동으로 현재 시간값이 들어감
	@PrePersist
	public void regdateDefault() {
		this.regdate = LocalDateTime.now();
	}
	
	public void addAuthority(Authority authority) {
		this.authorities.add(authority);
	}
	
}
