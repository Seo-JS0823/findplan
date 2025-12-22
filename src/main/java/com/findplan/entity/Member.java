package com.findplan.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
@SQLDelete(sql = "UPDATE member SET deleted = 'Y' WHERE code = ?")
@SQLRestriction("deleted = 'N'")
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long code;
	
	@Column(name = "email", unique = true, nullable = false)
	private String email;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "nickname", nullable = false, unique = true)
	private String nickname;
	
	@Column(name = "create_at", nullable = false)
	private LocalDateTime createAt;
	
	@Column(name = "role", nullable = false)
	private String role;
	
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean deleted = false;
	
	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MemberDevice> memberDevices = new ArrayList<>();
	
	public void updatePassword(String password) {
		this.password = password;
	}
	
	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}
	
}
