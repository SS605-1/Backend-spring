package com.ss6051.backendspring.schedule.actual;

import com.ss6051.backendspring.schedule.actual.domain.ActualWorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActualWorkScheduleRepository extends JpaRepository<ActualWorkSchedule, Long> {
}
