package com.ss6051.backendspring.schedule.actual.dto;

/**
 * 실제 근무 시간을 생성하기 위한 DTO
 *
 * @param id
 * @param updateDto 수정할 실제 근무 시간 DTO
 */
public record ActualWorkDeleteDTO(
        long id,
        ActualWorkDTO updateDto) {
}
