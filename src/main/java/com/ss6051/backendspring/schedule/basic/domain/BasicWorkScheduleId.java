package com.ss6051.backendspring.schedule.basic.domain;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.DayOfWeek;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BasicWorkScheduleId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

}
