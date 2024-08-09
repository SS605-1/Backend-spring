package com.ss6051.backendspring.schedule.dto.actual;

import java.util.List;

/**
 * 실제 근무 시간을 생성하기 위한 DTO
 *
 * @param accountId     계정 ID
 * @param storeId       매장 ID
 * @param actualWorkDTO 실제 근무 시간 DTO
 */
public record ActualWorkUpdateDTO(
        long accountId,
        long storeId,
        List<ActualWorkDTO> actualWorkDTO) {
}
