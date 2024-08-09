package com.ss6051.backendspring.store.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Address {
    private String street_address; // 도로명주소
    private String lot_number_address; // 지번주소
}
