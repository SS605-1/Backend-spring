package com.ss6051.backendspring.schedule.actual.dto;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.validator.ExistsInDatabase;
import com.ss6051.backendspring.store.domain.Store;

/**
 * 실제 근무 시간을 생성하기 위한 DTO
 *
 * @param accountId     계정 ID
 * @param storeId       매장 ID
 * @param actualWorkDTO 실제 근무 시간 DTO
 */
public record ActualWorkCreationDTO(
        @ExistsInDatabase(type = Account.class) long accountId,
        @ExistsInDatabase(type = Store.class) long storeId,
        ActualWorkDTO actualWorkDTO) {
}
