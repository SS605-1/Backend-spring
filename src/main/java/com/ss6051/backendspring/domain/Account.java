package com.ss6051.backendspring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

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

    // Service
    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;

}
