package com.ss6051.backendspring.schedule.basic;

import com.ss6051.backendspring.auth.exception.EntityNotFoundByIdException;
import com.ss6051.backendspring.schedule.basic.domain.BasicWorkSchedule;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkCreationDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkDeleteDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkReadDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule/basic")
@Slf4j
@RequiredArgsConstructor
public class BasicWorkScheduleController {

    private final BasicWorkScheduleService basicWorkScheduleService;

    @PostMapping("")
    public ResponseEntity<?> createBasicWorkSchedule(@RequestBody BasicWorkCreationDTO dto) {
        log.info("createBasicWorkSchedule");

        List<BasicWorkSchedule> schedules = null;
        try {
            schedules = basicWorkScheduleService.createBasicWorkSchedule(dto);
        } catch (EntityNotFoundByIdException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedules);
    }

    @GetMapping("")
    public ResponseEntity<?> findAllBasicWorkScheduleByStoreId(@RequestBody BasicWorkReadDTO dto) {
        log.info("findAllBasicWorkScheduleByStoreId");
        List<BasicWorkSchedule> schedules = null;
        try {
            schedules = basicWorkScheduleService.findAllBasicWorkSchedule(dto);
        } catch (EntityNotFoundByIdException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedules);
    }

    @PatchMapping("")
    public ResponseEntity<?> updateBasicWorkSchedule(@RequestBody BasicWorkUpdateDTO dto) {
        log.info("updateBasicWorkSchedule");

        BasicWorkSchedule schedule = null;
        try {
            schedule = basicWorkScheduleService.updateBasicWorkSchedule(dto);
        } catch (EntityNotFoundByIdException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedule);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteBasicWorkSchedule(@RequestBody BasicWorkDeleteDTO dto) {
        log.info("deleteBasicWorkSchedule");

        basicWorkScheduleService.deleteBasicWorkSchedule(dto);

        return ResponseEntity.ok().build();
    }

}
