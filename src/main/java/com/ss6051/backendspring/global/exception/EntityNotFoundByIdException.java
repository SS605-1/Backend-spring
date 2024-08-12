package com.ss6051.backendspring.global.exception;

public class EntityNotFoundByIdException extends RuntimeException {
    public EntityNotFoundByIdException(String message) {
        super(message);
    }

    public EntityNotFoundByIdException(String entityTypeStr, long value) {
        super(String.format("%s type %d 값이 db에 존재하지 않습니다", entityTypeStr, value));
    }
}
