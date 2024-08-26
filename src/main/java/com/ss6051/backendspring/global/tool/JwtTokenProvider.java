package com.ss6051.backendspring.global.tool;

import com.ss6051.backendspring.Secret;
import com.ss6051.backendspring.global.domain.Account;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;
    private static final String secretKey = Secret.JWT_SECRET;

    public JwtTokenProvider() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Spring Security 에서 로그인한 사용자의 ID를 가져온다.
     */
    public static long getAccountIdFromSecurity() {
        return ((Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public String accessTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)    //uid
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String refreshTokenGenerate(Date expiredAt) {
        // refresh token은 uid가 없음: 만료 시간이 길어 보안상 문제가 생길 수 있어 제외
        return Jwts.builder()
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

}