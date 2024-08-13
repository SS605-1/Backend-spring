package com.ss6051.backendspring.global.validator;

import com.ss6051.backendspring.account.AccountService;
import com.ss6051.backendspring.global.exception.CustomException;
import com.ss6051.backendspring.global.exception.ErrorCode;
import com.ss6051.backendspring.store.StoreService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        switch (entityType.getSimpleName()) {
            case "Account" -> accountService.findAccount(value);
            case "Store" -> storeService.findStore(value);
            default -> {
                log.error("존재하지 않는 엔티티 검증 오류 발생: {}", entityType.getSimpleName());
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        return true;
    }
}
