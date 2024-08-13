package com.ss6051.backendspring.schedule.actual;

import com.ss6051.backendspring.global.exception.CustomException;
import com.ss6051.backendspring.schedule.actual.domain.ActualWorkSchedule;
import com.ss6051.backendspring.schedule.actual.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "실제 근무 스케줄 생성",
            description = "매장의 실제 근무 스케줄을 생성합니다. 근무 스케줄 조회와 동일한 결과를 반환합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "실제 근무 스케줄 생성 성공",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(value = """
                                            [
                                                {
                                                   "id": 1,
                                                   "schedule": {
                                                     "id": 10, // Schedule의 id
                                                     "otherFields": "value" // Schedule 엔티티의 다른 필드들. 무시해도 됨.
                                                   },
                                                   "account": {
                                                     "id": 20, // Account의 id
                                                     "otherFields": "value" // Account 엔티티의 다른 필드들. 무시해도 됨.
                                                   },
                                                   "dayOfWeek": "MONDAY", // DayOfWeek 열거형 값
                                                   "startTime": "09:00:00", // LocalTime 포맷
                                                   "endTime": "17:00:00" // LocalTime 포맷
                                                }
                                            ]
                                            """)}))
            }
    )
    @PostMapping("")
    public ResponseEntity<?> createActualWorkSchedule(@RequestBody ActualWorkCreationDTO dto) {
        log.info("createActualWorkSchedule");

        try {
            actualWorkScheduleService.createActualWorkSchedule(dto);
        } catch (CustomException e) {
            log.error(e.getMessage());
        }

        return findAllActualWorkScheduleByStoreIdAndAccountId(new ActualWorkReadStoreAccountAllDTO(dto.storeId(), dto.accountId()));
    }

    @Operation(summary = "특정 매장에 근무중인 한 명의 직원의 실제 근무 스케줄 조회",
            description = "특정 매장에 근무중인 한 명의 직원의 실제 근무 스케줄을 조회합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "실제 근무 스케줄 조회 성공",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(value = """
                                            [
                                                {
                                                   "id": 1,
                                                   "schedule": {
                                                     "id": 10, // Schedule의 id
                                                     "otherFields": "value" // Schedule 엔티티의 다른 필드들. 무시해도 됨.
                                                   },
                                                   "account": {
                                                     "id": 20, // Account의 id
                                                     "otherFields": "value" // Account 엔티티의 다른 필드들. 무시해도 됨.
                                                   },
                                                   "dayOfWeek": "MONDAY", // DayOfWeek 열거형 값
                                                   "startTime": "09:00:00", // LocalTime 포맷
                                                   "endTime": "17:00:00" // LocalTime 포맷
                                                }
                                            ]
                                            """)}))
            }
    )
    @GetMapping("/store-and-account")
    public ResponseEntity<?> findAllActualWorkScheduleByStoreIdAndAccountId(@RequestBody ActualWorkReadStoreAccountAllDTO dto) {
        log.info("findAllActualWorkScheduleByStoreIdAndAccountId");

        List<ActualWorkSchedule> schedules = null;
        try {
            schedules = actualWorkScheduleService.findAllActualWorkScheduleByStoreIdAndAccountId(dto);
        } catch (CustomException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "특정 매장에 근무 중인 모든 직원의 실제 근무 스케줄 조회",
            description = "특정 매장에 근무 중인 모든 직원의 실제 근무 스케줄을 조회합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "실제 근무 스케줄 조회 성공",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(value = """
                                            [
                                                {
                                                   "id": 1,
                                                   "schedule": {
                                                     "id": 10, // Schedule의 id
                                                     "otherFields": "value" // Schedule 엔티티의 다른 필드들. 무시해도 됨.
                                                   },
                                                   "account": {
                                                     "id": 20, // Account의 id
                                                     "otherFields": "value" // Account 엔티티의 다른 필드들. 무시해도 됨.
                                                   },
                                                   "dayOfWeek": "MONDAY", // DayOfWeek 열거형 값
                                                   "startTime": "09:00:00", // LocalTime 포맷
                                                   "endTime": "17:00:00" // LocalTime 포맷
                                                }
                                            ]
                                            """)}))
            }
    )
    @GetMapping("/store")
    public ResponseEntity<?> findAllActualWorkScheduleByStoreId(@RequestBody ActualWorkReadStoreAllDTO dto) {
        log.info("findAllActualWorkScheduleByStoreId");
        List<ActualWorkSchedule> schedules = null;
        try {
            schedules = actualWorkScheduleService.findAllActualWorkScheduleByStoreId(dto);
        } catch (CustomException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "특정 직원의 실제 근무 스케줄 조회",
            description = "특정 직원의 실제 근무 스케줄을 조회합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "실제 근무 스케줄 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActualWorkSchedule.class)))
            }
    )
    @PatchMapping("")
    public ResponseEntity<?> updateActualWorkSchedule(@RequestBody ActualWorkUpdateDTO dto) {
        log.info("updateActualWorkSchedule");

        ActualWorkSchedule schedule = null;
        try {
            schedule = actualWorkScheduleService.updateActualWorkSchedule(dto);
        } catch (CustomException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "실제 근무 스케줄 삭제",
            description = "매장의 실제 근무 스케줄을 삭제합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "실제 근무 스케줄 삭제 성공")
            }
    )
    @DeleteMapping("")
    public ResponseEntity<?> deleteActualWorkSchedule(@RequestBody ActualWorkDeleteDTO dto) {
        log.info("deleteActualWorkSchedule");

        actualWorkScheduleService.deleteActualWorkSchedule(dto);

        return ResponseEntity.ok().build();
    }

}
