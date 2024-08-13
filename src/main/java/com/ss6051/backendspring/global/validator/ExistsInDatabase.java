package com.ss6051.backendspring.global.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsInDatabaseValidator.class)
public @interface ExistsInDatabase {
    String message() default "The value does not exist in the database.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    Class<?> type(); // 검증할 엔티티 타입
}
