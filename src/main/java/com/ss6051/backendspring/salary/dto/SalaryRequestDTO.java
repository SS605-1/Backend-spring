package com.ss6051.backendspring.salary.dto;

import java.time.LocalDate;

public record SalaryRequestDTO(Long accountId, Long storeId, LocalDate startDate, LocalDate endDate) {
}
