package com.findplan.domain.finance.model;

import java.time.LocalDateTime;

import com.findplan.domain.member.model.MemberEntity;

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
@Table(name = "ACCOUNT")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AccountEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "A_IDS")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "M_IDS", nullable = false)
	private MemberEntity member;
	
	@Column(name = "NAME", nullable = false, length = 15)
	private Long name;
	
	@Column(name = "BALANCE", nullable = false)
	private Long balance;
	
	@Column(name = "TYPE", nullable = false)
	private AccountType accountType;
	
	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "DELETED")
	private boolean deleted;
	
}
