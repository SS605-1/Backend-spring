package com.ss6051.backendspring.store.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.domain.Role;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    private Account owner; // 사장

    @OneToMany(mappedBy = "store")
    @Builder.Default
    private List<StoreAccount> managerList = new ArrayList<>(); // 직원 목록

    @OneToMany(mappedBy = "store")
    @Builder.Default
    private List<StoreAccount> employeeList = new ArrayList<>(); // 직원 목록

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Address address;
//
//    @Setter
//    @OneToOne(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private Schedule schedule;

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

    @Deprecated
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
                .baseSalary(0L)
                .build());
    }

    public void removeAccount(Account account) {
        managerList.removeIf(storeAccount -> storeAccount.getAccount().equals(account));
        employeeList.removeIf(storeAccount -> storeAccount.getAccount().equals(account));
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

    public long getEmployeeCount() {
        return employeeList.size();
    }
}