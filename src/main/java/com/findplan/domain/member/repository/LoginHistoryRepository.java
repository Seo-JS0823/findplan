package com.findplan.domain.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findplan.domain.member.model.LoginHistoryEntity;
import com.findplan.domain.member.model.MemberEntity;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistoryEntity, Long> {
	List<LoginHistoryEntity> findByMember(MemberEntity member);
}
