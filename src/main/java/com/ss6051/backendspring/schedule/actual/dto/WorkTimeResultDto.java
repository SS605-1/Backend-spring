package com.ss6051.backendspring.schedule.actual.dto;

public record WorkTimeResultDto(long dayShiftMinute,
                                long nightShiftMinute,
                                int workDayCount) {
}
