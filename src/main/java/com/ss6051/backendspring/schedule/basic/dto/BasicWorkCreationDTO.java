package com.ss6051.backendspring.schedule.basic.dto;

/**
 * 기본 근무 시간을 생성하기 위한 DTO
 *
 * @param storeId      매장 ID
 * @param accountId    계정 ID
 */
public record BasicWorkCreationDTO(
        long storeId,
        long accountId) {
}
