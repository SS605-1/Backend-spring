package com.ss6051.backendspring.schedule.basic.domain;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BasicWorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 복합 키

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule; // 스케줄과 매핑

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account; // 계정과 매핑

    @Enumerated(EnumType.STRING)
    @MapsId("dayOfWeek")
    private DayOfWeek dayOfWeek; // 요일

    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간

    public void update(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
