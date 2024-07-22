package com.ss6051.backendspring.dto;

import com.ss6051.backendspring.domain.Account;
import lombok.Data;

@Data
public class LoginResponseDto {
    public boolean isLoginSuccessful;
    public Account account;
}
