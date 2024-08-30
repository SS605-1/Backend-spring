package com.ss6051.backendspring.schedule.actual.dto;

import java.time.LocalDateTime;

public record ActualWorkTimeRequestDTO(long storeId,
                                       boolean hasMoreThanFiveEmployees,
                                       LocalDateTime startDateTime,
                                       LocalDateTime endDateTime) {
}
