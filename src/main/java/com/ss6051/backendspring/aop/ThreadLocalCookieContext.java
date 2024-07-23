package com.ss6051.backendspring.aop;

public class ThreadLocalCookieContext {
    private static final ThreadLocal<String> cookieValue = new ThreadLocal<>();

    public static void setCookieValue(String value) {
        cookieValue.set(value);
    }

    public static String getCookieValue() {
        return cookieValue.get();
    }

    public static void clear() {
        cookieValue.remove();
    }
}
