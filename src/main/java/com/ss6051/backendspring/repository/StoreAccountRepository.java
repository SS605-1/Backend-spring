package com.ss6051.backendspring.repository;

import com.ss6051.backendspring.domain.StoreAccount;
import com.ss6051.backendspring.domain.StoreAccountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreAccountRepository extends JpaRepository<StoreAccount, StoreAccountId> {
    Optional<StoreAccount> findByStoreIdAndAccountId(Long storeId, Long accountId);
}
