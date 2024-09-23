package com.ss6051.backendspring.schedule.common.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.actual.domain.ActualWorkSchedule;
import com.ss6051.backendspring.schedule.basic.domain.BasicWorkSchedule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Schedule {

    @Id
    private Long id; // ID

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasicWorkSchedule> basicWorkSchedules; // 기본 근무 일정

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActualWorkSchedule> actualWorkSchedules; // 실제 근무 일정

    @Column(name = "last_modified_time")
    private LocalDateTime lastModifiedTime; // 마지막 변경 시각

    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    private Account lastModifiedBy; // 마지막으로 변경한 사람
}
