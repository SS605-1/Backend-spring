package com.ss6051.backendspring.global.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.ss6051.backendspring.Secret.FRONTEND_EIP;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://" + FRONTEND_EIP, "https://" + FRONTEND_EIP, "http://" + FRONTEND_EIP + ":3000", "https://" + FRONTEND_EIP + ":3000")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}
