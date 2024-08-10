package com.ss6051.backendspring.schedule.common;

import com.ss6051.backendspring.schedule.common.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
