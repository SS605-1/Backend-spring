package com.ss6051.backendspring.schedule.basic.dto;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.validator.ExistsInDatabase;
import com.ss6051.backendspring.store.domain.Store;

/**
 * 기본 근무 시간을 생성하기 위한 DTO
 *
 * @param storeId   매장 ID
 * @param accountId 계정 ID
 * @param basicWorkDTO 기본 근무 시간 DTO
 */
public record BasicWorkUpdateDTO(
        @ExistsInDatabase(type = Store.class) long storeId,
        @ExistsInDatabase(type = Account.class) long accountId,
        BasicWorkDTO basicWorkDTO) {
}
