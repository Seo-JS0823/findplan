package com.findplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findplan.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	
	boolean existsByEmail(String email);
	
	boolean existsByNickname(String nickname);
}
