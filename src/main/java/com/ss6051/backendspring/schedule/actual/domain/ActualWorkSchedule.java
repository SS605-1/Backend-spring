package com.ss6051.backendspring.schedule.actual.domain;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActualWorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private LocalDateTime startDateTime; // 실제 근무 시작 일시
    private LocalDateTime endDateTime;   // 실제 근무 종료 일시

    public Long getActualWorkTimeMinute() {
        return Duration.between(startDateTime, endDateTime).toMinutes(); // 초 단위 절사
    }

    public void update(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void updateEnd(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

}

