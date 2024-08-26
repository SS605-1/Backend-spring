package com.ss6051.backendspring.store.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String streetAddress; // 도로명주소
    private String lotNumberAddress; // 지번주소

    @OneToOne(mappedBy = "address")
    @JsonIgnore
    private Store store;
}
