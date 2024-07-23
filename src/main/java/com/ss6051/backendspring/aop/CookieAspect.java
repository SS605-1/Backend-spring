package com.ss6051.backendspring.aop;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CookieAspect {

    /**
     * account_id 쿠키 값 추출을 위한 Aspect
     * StoreController 내 모든 메소드 호출 전에 실행
     */

    @SuppressWarnings("FieldCanBeLocal")
    private final String targetCookieName = "account_id";
    private static final String targetPath = "execution(* com.ss6051.backendspring.controller.StoreController.*(..))";

    @Before(targetPath) // StoreController 내 모든 메소드 호출 전에 실행
    public void addCookieValue(JoinPoint joinPoint) {
        HttpServletRequest request = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
                break;
            }
        }

        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (targetCookieName.equals(cookie.getName())) {
                        ThreadLocalCookieContext.setCookieValue(cookie.getValue());
                    }
                }
            }
        }
    }

    /**
     * ThreadLocal 메모리 누수 방지를 위해 요청 종료 후 ThreadLocal 값 정리
     */
    @After(targetPath)
    public void clearCookieValue() {
        ThreadLocalCookieContext.clear();
    }
}

