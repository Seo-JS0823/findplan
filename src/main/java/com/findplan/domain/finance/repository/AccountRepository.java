package com.findplan.domain.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findplan.domain.finance.model.AccountEntity;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

}
