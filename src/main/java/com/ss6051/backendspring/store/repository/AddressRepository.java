package com.ss6051.backendspring.store.repository;

import com.ss6051.backendspring.store.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    boolean existsByStreet_addressOrLot_number_address(String street_address, String lot_number_address);
}
