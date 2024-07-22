package com.ss6051.backendspring.controller;

import com.ss6051.backendspring.dto.KakaoAccessTokenDto;
import com.ss6051.backendspring.dto.LoginResponseDto;
import com.ss6051.backendspring.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/oauth2/kakao")
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    /**
     * 카카오 로그인
     * @param request HttpServletRequest
     * @return ResponseEntity<LoginResponseDto>
     */
    @GetMapping("/")
    public ResponseEntity<LoginResponseDto> kakaoLogin(HttpServletRequest request) {
        log.info("kakaoLogin() start");
        String code = request.getParameter("code");
        KakaoAccessTokenDto kakaoAccessToken = authService.getKakaoAccessToken(code);
        ResponseEntity<LoginResponseDto> ret = authService.kakaoLogin(kakaoAccessToken.getAccess_token());
        log.info("kakaoLogin() end");
        return ret;
    }

}
