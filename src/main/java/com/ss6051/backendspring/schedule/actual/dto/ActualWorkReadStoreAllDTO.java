package com.ss6051.backendspring.schedule.actual.dto;

import com.ss6051.backendspring.global.validator.ExistsInDatabase;
import com.ss6051.backendspring.store.domain.Store;

public record ActualWorkReadStoreAllDTO(
        @ExistsInDatabase(type = Store.class) long storeId) {
}
