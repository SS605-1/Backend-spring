package com.ss6051.backendspring.store.repository;

import com.ss6051.backendspring.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

}
