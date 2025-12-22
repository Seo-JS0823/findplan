package com.findplan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.findplan.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	
	Optional<Member> findByEmail(String email);
	
	@Query(value = "SELECT COUNT(*) > 0 FROM member WHERE email = :email AND deleted = 'Y'", nativeQuery = true)
	boolean existMember(@Param("email") String email);
	
	boolean existsByEmail(String email);
	
	boolean existsByNickname(String nickname);
}
