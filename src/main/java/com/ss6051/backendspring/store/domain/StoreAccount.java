package com.ss6051.backendspring.store.domain;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.domain.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(StoreAccountId.class)
public class StoreAccount {

    @Id
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Id
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;

    @Setter
    private Long baseSalary;

}

