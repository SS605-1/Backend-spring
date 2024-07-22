package com.ss6051.backendspring.dto;

import com.ss6051.backendspring.entity.Account;
import lombok.Data;

@Data
public class LoginResponseDto {
    public boolean isLoginSuccessful;
    public Account account;
}
