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

    /**
     * 매장에 속한 모든 계정(사장, 관리자, 직원)을 조회한다.
     * @return List<Account> 매장에 속한 모든 계정
     */
    public List<Account> getAllAccounts() {
        List<Account> allAccounts = managerList;
        allAccounts.add(boss);
        allAccounts.addAll(employeeList);
        return allAccounts;
    }

    /**
     * 매장에 속한 모든 관리자 계정(사장과 관리자 계정)을 조회한다.
     * @return List<Account> 매장에 속한 모든 관리자 계정
     */
    public List<Account> getManageableAccounts() {
        List<Account> manageableAccounts = managerList;
        manageableAccounts.add(boss);
        return manageableAccounts;
    }

    public void addEmployee(Account account) {
        if (employeeList == null) {
            employeeList = List.of(account);
            return;
        }
        employeeList.add(account);
    }
}