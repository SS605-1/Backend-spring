package com.ss6051.backendspring.schedule.actual;

import com.ss6051.backendspring.global.exception.EntityNotFoundByIdException;
import com.ss6051.backendspring.schedule.actual.domain.ActualWorkSchedule;
import com.ss6051.backendspring.schedule.actual.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule/actual")
@Slf4j
@RequiredArgsConstructor
public class ActualWorkScheduleController {

    private final ActualWorkScheduleService actualWorkScheduleService;

    @PostMapping("")
    public ResponseEntity<?> createActualWorkSchedule(@RequestBody ActualWorkCreationDTO dto) {
        log.info("createActualWorkSchedule");

        try {
            actualWorkScheduleService.createActualWorkSchedule(dto);
        } catch (EntityNotFoundByIdException e) {
            log.error(e.getMessage());
        }

        return findAllActualWorkScheduleByStoreIdAndAccountId(new ActualWorkReadStoreAccountAllDTO(dto.storeId(), dto.accountId()));
    }

    @GetMapping("/store-and-account")
    public ResponseEntity<?> findAllActualWorkScheduleByStoreIdAndAccountId(@RequestBody ActualWorkReadStoreAccountAllDTO dto) {
        log.info("findAllActualWorkScheduleByStoreIdAndAccountId");

        List<ActualWorkSchedule> schedules = null;
        try {
            schedules = actualWorkScheduleService.findAllActualWorkScheduleByStoreIdAndAccountId(dto);
        } catch (EntityNotFoundByIdException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/store")
    public ResponseEntity<?> findAllActualWorkScheduleByStoreId(@RequestBody ActualWorkReadStoreAllDTO dto) {
        log.info("findAllActualWorkScheduleByStoreId");
        List<ActualWorkSchedule> schedules = null;
        try {
            schedules = actualWorkScheduleService.findAllActualWorkScheduleByStoreId(dto);
        } catch (EntityNotFoundByIdException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedules);
    }

    @PatchMapping("")
    public ResponseEntity<?> updateActualWorkSchedule(@RequestBody ActualWorkUpdateDTO dto) {
        log.info("updateActualWorkSchedule");

        ActualWorkSchedule schedule = null;
        try {
            schedule = actualWorkScheduleService.updateActualWorkSchedule(dto);
        } catch (EntityNotFoundByIdException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedule);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteActualWorkSchedule(@RequestBody ActualWorkDeleteDTO dto) {
        log.info("deleteActualWorkSchedule");

        actualWorkScheduleService.deleteActualWorkSchedule(dto);

        return ResponseEntity.ok().build();
    }

}
