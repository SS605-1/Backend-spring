package com.ss6051.backendspring.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToOne
    private Account boss; // 사장

    @OneToMany
    private List<Account> managerList; // 관리자

    @OneToMany
    private List<Account> employeeList; // 직원

    @Embedded
    private Address address;

}