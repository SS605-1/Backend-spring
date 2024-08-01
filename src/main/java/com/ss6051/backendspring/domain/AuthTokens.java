package com.ss6051.backendspring.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;
}