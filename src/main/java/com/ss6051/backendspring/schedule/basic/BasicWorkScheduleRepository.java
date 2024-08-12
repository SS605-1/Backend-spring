package com.ss6051.backendspring.schedule.basic;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.basic.domain.BasicWorkSchedule;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface BasicWorkScheduleRepository extends JpaRepository<BasicWorkSchedule, Long> {
    List<BasicWorkSchedule> findAllByScheduleAndAccount(Schedule schedule, Account account);
    Optional<BasicWorkSchedule> findByScheduleAndAccountAndDayOfWeek(Schedule schedule, Account account, DayOfWeek dayOfWeek);
}
