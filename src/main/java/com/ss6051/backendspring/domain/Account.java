package com.ss6051.backendspring.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Account implements UserDetails {

    @Id
    private Long id;

    private String nickname;
    private String profile_image_url;
    private String thumbnail_image_url;

    @OneToMany(mappedBy = "account")
    private List<StoreAccount> storeAccounts; // 매장별 역할 정보 리스트

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return storeAccounts.stream()
                .map(storeAccount -> new SimpleGrantedAuthority("STORE_" + storeAccount.getStore().getId() + "_ROLE_" + storeAccount.getRole().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null; // 패스워드가 필요한 경우 구현
    }

    @Override
    public String getUsername() {
        return nickname;
    }

}
