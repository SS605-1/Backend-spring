package com.ss6051.backendspring.schedule.actual.dto;

import java.time.LocalDate;

public record ActualWorkTimeRequestDTO(long storeId,
                                       long accountId,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       int criteriaHour) {
}
