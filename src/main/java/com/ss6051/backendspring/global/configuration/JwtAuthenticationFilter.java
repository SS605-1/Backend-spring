package com.ss6051.backendspring.global.configuration;

import com.ss6051.backendspring.account.AccountService;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.exception.CustomException;
import com.ss6051.backendspring.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Filter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

import static com.ss6051.backendspring.Secret.JWT_SECRET;

@Slf4j
@Filter(name = "JwtAuthenticationFilter")
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccountService accountService;
    private final SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter.doFilterInternal start");
        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token) && validateToken(token)) {
            String uid = getUserIdFromJWT(token);

            Account account = accountService.findAccount(Long.parseLong(uid));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    account, null, account.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        log.info("JwtAuthenticationFilter.doFilterInternal end");
        filterChain.doFilter(request, response);
    }

    private String getUserIdFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
        } catch (ExpiredJwtException ex) {
            throw new CustomException(ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (MalformedJwtException | UnsupportedJwtException |
                 IllegalArgumentException | SignatureException ex) {
            // Invalid JWT token
            // Unsupported JWT token
            // JWT claims string is empty
            // JWT signature does not match locally computed signature
            log.error("JWT token error: {}", ex.getMessage());
            throw new CustomException(ErrorCode.JWT_TOKEN_INVALID, ex.getClass().getName());
        }
        return true;
    }

}
