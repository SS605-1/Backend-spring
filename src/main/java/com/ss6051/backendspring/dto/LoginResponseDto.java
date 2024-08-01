package com.ss6051.backendspring.dto;

import com.ss6051.backendspring.domain.AuthTokens;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private Long id;
    private String nickname;
    private String profile_image_url;
    private String thumbnail_image_url;

    private AuthTokens token;
}
