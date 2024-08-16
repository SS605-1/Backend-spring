package com.ss6051.backendspring.schedule.actual;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.actual.domain.ActualWorkSchedule;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActualWorkScheduleRepository extends JpaRepository<ActualWorkSchedule, Long> {
    List<ActualWorkSchedule> findAllBySchedule(Schedule schedule);
    List<ActualWorkSchedule> findAllByScheduleAndAccount(Schedule schedule, Account account);

}
