package com.ss6051.backendspring.schedule.dto.basic;

import java.util.List;

/**
 * 기본 근무 시간을 생성하기 위한 DTO
 *
 * @param storeId   매장 ID
 * @param accountId 계정 ID
 * @param basicWorkDTO 기본 근무 시간 DTO
 */
public record BasicWorkUpdateDTO(
        long storeId,
        long accountId,
        List<BasicWorkDTO> basicWorkDTO) {
}