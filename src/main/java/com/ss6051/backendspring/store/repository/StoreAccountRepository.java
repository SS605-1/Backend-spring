package com.ss6051.backendspring.store.repository;

import com.ss6051.backendspring.store.domain.StoreAccount;
import com.ss6051.backendspring.store.domain.StoreAccountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreAccountRepository extends JpaRepository<StoreAccount, StoreAccountId> {
    Optional<StoreAccount> findByStoreIdAndAccountId(Long storeId, Long accountId);
}
