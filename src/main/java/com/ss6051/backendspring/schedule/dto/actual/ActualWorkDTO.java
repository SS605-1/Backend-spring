package com.ss6051.backendspring.schedule.dto.actual;

import java.time.LocalDateTime;

/**
 * 기본 근무 시간을 생성하기 위한 DTO
 *
 * @param startDateTime 시작 일시
 * @param endDateTime   종료 일시
 */
public record ActualWorkDTO(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime) {
}
