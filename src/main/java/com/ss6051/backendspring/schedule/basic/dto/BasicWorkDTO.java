package com.ss6051.backendspring.schedule.basic.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * 기본 근무 시간을 생성하기 위한 DTO
 *
 * @param dayOfWeek 요일
 * @param startTime 시작 시간
 * @param endTime   종료 시간
 */
public record BasicWorkDTO(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime) {
}
