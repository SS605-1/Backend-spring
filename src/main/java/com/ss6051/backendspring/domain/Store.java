package com.ss6051.backendspring.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Stream;

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
    private Account owner; // 사장

    @OneToMany(mappedBy = "store")
    private List<StoreAccount> managerList; // 직원 목록

    @OneToMany(mappedBy = "store")
    private List<StoreAccount> employeeList; // 직원 목록


    @Embedded
    private Address address;

    /**
     * 매장에 속한 모든 계정(사장, 관리자, 직원)을 조회한다.
     * @return List<Account> 매장에 속한 모든 계정
     */
    public List<Account> getAllAccounts() {
        return getStoreAccountStream()
                .map(StoreAccount::getAccount).toList();
    }

    /**
     * 매장에 속한 모든 관리자 계정(사장과 관리자 계정)을 조회한다.
     * @return List<Account> 매장에 속한 모든 관리자 계정
     */
    public List<Account> getManageableAccounts() {
        return getStoreAccountStream()
                .filter(storeAccount -> storeAccount.getRole().isManageable()) // Manager 권한 이상
                .map(StoreAccount::getAccount).toList();

    }

    public boolean isNotManageableAccount(Account account) {
        return !getManageableAccounts().contains(account);
    }

    /**
     * 매장에 새 직원을 추가한다.
     * @param account 직원으로 추가할 계정
     */
    public void addEmployee(Account account) {
        employeeList.add(StoreAccount.builder()
                .store(this)
                .account(account)
                .role(Role.EMPLOYEE)
                .build());
    }

    /**
     * 해당 매장 소속 계정을 조회하는 스트림을 반환한다.
     */
    private Stream<StoreAccount> getStoreAccountStream() {
        // return owner + managerList + employeeList
        return Stream.concat(
                Stream.of(StoreAccount.builder()
                        .store(this)
                        .account(owner)
                        .role(Role.OWNER)
                        .build()),
                Stream.concat(
                        managerList.stream(),
                        employeeList.stream()
                )
        );
    }
}