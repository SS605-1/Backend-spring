package com.ss6051.backendspring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/oauth2/kakao/**").permitAll() // OAuth2 로그인 요청 허용
                                .requestMatchers("/", "/swagger").permitAll() // Swagger UI 리다이렉트 경로 접근 허용
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 접근 허용
                                .anyRequest().authenticated() // 기타 요청은 인증 필요
                ).csrf(AbstractHttpConfigurer::disable); // CSRF 보안 기능 비활성화
//                .formLogin(withDefaults()); // 기본 로그인 페이지 사용

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // In-memory user store with username and password
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password("{noop}password") // `{noop}`은 패스워드 인코딩을 하지 않겠다는 의미
                        .roles("USER")
                        .build()
        );
    }
}
