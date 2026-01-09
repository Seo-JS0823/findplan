package com.findplan.domain.member.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.findplan.domain.member.model.LoginHistoryEntity;
import com.findplan.domain.member.model.MemberEntity;
import com.findplan.domain.member.repository.LoginHistoryRepository;
import com.findplan.domain.member.repository.MemberRepository;
import com.findplan.domain.member.transfer.response.LoginHistoryResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginHistoryService {

	private final LoginHistoryRepository logHisRepo;
	
	private final MemberRepository memberRepo;
	
	public List<LoginHistoryResponse> findLoginHistoryAll() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		MemberEntity member = memberRepo.findByEmail(email);
		
		List<LoginHistoryEntity> histories = logHisRepo.findByMember(member);
		
		return histories.stream()
				.map(LoginHistoryEntity::toResponse)
				.toList();
	}
	
}
