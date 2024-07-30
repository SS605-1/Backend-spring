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
public class Account {

    // Kakao OAuth2
    @Id
    private Long id;

    private String nickname;
    private String profile_image_url;
    private String thumbnail_image_url;

    @ManyToMany(mappedBy = "managerList")
    private List<Store> managedStores; // 관리하는 매장 목록

    @ManyToMany(mappedBy = "employeeList")
    private List<Store> employedStores; // 직원으로 근무하는 매장 목록

}
