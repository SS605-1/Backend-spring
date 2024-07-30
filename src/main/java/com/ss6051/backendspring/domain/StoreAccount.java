package com.ss6051.backendspring.domain;

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

}

