package com.ss6051.backendspring.controller;

import com.ss6051.backendspring.dto.LoginResponseDto;
import com.ss6051.backendspring.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/oauth2/kakao")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 로그인
     * @param request HttpServletRequest
     * @return {@code ResponseEntity<LoginResponseDto>} 카카오 로그인 성공 시 사용자 정보를 담은 ResponseEntity. 실패 시 빈 ResponseEntity
     */
    @PostMapping("")
    public ResponseEntity<LoginResponseDto> kakaoLogin(HttpServletRequest request) {
        log.info("kakaoLogin() start");
        String code = request.getParameter("code");

        ResponseEntity<LoginResponseDto> ret = authService.kakaoLogin(code);
        log.info("kakaoLogin() end");
        return ret;
    }


}
