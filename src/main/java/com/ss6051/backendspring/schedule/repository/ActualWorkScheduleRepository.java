package com.ss6051.backendspring.schedule.repository;

import com.ss6051.backendspring.schedule.domain.ActualWorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActualWorkScheduleRepository extends JpaRepository<ActualWorkSchedule, Long> {
}
