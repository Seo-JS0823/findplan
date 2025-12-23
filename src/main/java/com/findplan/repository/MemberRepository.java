package com.findplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.findplan.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	
	boolean existsByEmail(String email);
	
	boolean existsByNickname(String nickname);

	Member findByEmail(String email);
	
	@Query("SELECT m FROM Member m LEFT JOIN FETCH m.devices	WHERE m.email = :email")
	Member findByEmailWithDevices(@Param("email") String email);
}
