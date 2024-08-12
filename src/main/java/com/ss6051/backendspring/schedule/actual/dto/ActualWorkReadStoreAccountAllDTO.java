package com.ss6051.backendspring.schedule.actual.dto;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.validator.ExistsInDatabase;
import com.ss6051.backendspring.store.domain.Store;

public record ActualWorkReadStoreAccountAllDTO(
        @ExistsInDatabase(type = Store.class) long storeId,
        @ExistsInDatabase(type = Account.class) long accountId) {
}
