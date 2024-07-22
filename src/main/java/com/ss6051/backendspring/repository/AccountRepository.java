package com.ss6051.backendspring.repository;

import com.ss6051.backendspring.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
