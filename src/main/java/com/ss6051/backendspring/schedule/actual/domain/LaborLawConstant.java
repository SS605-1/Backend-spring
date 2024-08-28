package com.ss6051.backendspring.schedule.actual.domain;

import lombok.Getter;

@Getter
public enum LaborLawConstant {
    DAY_SHIFT_START_HOUR(6),
    DAY_SHIFT_END_HOUR(22),
    NIGHT_SHIFT_START_HOUR(22),
    NIGHT_SHIFT_END_HOUR(6),
    FIVE_EMPLOYEES_STORE(5);

    private final int value;

    LaborLawConstant(int value) {
        this.value = value;
    }

}
