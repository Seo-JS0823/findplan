package com.findplan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findplan.model.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
	List<Authority> findByMemberCode(Long code);
}
