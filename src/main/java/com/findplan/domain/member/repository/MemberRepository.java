package com.findplan.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.findplan.domain.member.model.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	
	@Query("SELECT m FROM MemberEntity m LEFT JOIN FETCH m.devices WHERE m.email = :email")
	MemberEntity findByEmailWithDevices(@Param("email") String email);
	
	MemberEntity findByEmail(String email);
	
	boolean existsByEmail(String email);
	
	boolean existsByNickname(String nickname);
}
