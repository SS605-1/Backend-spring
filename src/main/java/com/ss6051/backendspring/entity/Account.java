package com.ss6051.backendspring.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Account {

    @Id
    private Long id;

    private String nickname;
    private String profile_image_url;
    private String thumbnail_image_url;
}
