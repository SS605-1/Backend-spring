package com.ss6051.backendspring.store.repository;

import com.ss6051.backendspring.store.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    boolean existsByStreetAddressOrLotNumberAddress(String street_address, String lot_number_address);
}
