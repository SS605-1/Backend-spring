package com.ss6051.backendspring.global.validator;

import com.ss6051.backendspring.account.AccountService;
import com.ss6051.backendspring.global.exception.EntityNotFoundByIdException;
import com.ss6051.backendspring.store.StoreService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class ExistsInDatabaseValidator implements ConstraintValidator<ExistsInDatabase, Long> {

    private Class<?> entityType;

    private final AccountService accountService;
    private final StoreService storeService;

    @Override
    public void initialize(ExistsInDatabase constraintAnnotation) {
        this.entityType = constraintAnnotation.type();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null 값 허용 시, 검증 통과
        }

        boolean exists = switch (entityType.getSimpleName()) {
            case "Account" -> accountService.findAccount(value).isPresent();
            case "Store" -> storeService.findStore(value).isPresent();
            default -> false;
        };

        if (!exists) {
            String errorMessage = String.format("%s type %d 값이 db에 존재하지 않습니다", entityType.getSimpleName(), value);
            throw new EntityNotFoundByIdException(errorMessage);
        }

        return true;
    }
}
