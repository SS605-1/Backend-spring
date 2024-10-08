package com.ss6051.backendspring.account;

import com.ss6051.backendspring.global.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
