package com.findplan.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findplan.member.entity.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	Optional<MemberEntity> findByMemberEmail(String memberEmail);

	boolean existsByMemberEmail(String email);
	
	boolean existsByMemberNickname(String nickname);
}
