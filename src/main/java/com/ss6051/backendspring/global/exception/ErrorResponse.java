package com.ss6051.backendspring.global.exception;

public record ErrorResponse(String errorCode, String errorMessage, String payload) {
}
