package com.ss6051.backendspring.schedule.basic;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.basic.domain.BasicWorkSchedule;
import com.ss6051.backendspring.schedule.basic.domain.BasicWorkScheduleId;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicWorkScheduleRepository extends JpaRepository<BasicWorkSchedule, BasicWorkScheduleId> {
    List<BasicWorkSchedule> findAllByScheduleAndAccount(Schedule schedule, Account account);
}
