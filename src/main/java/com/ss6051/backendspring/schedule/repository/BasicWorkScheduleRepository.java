package com.ss6051.backendspring.schedule.repository;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.domain.BasicWorkSchedule;
import com.ss6051.backendspring.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicWorkScheduleRepository extends JpaRepository<BasicWorkSchedule, Long> {
    List<BasicWorkSchedule> findAllByScheduleAndAccount(Schedule schedule, Account account);
}
