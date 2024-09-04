package com.ss6051.backendspring.schedule.basic;

import com.ss6051.backendspring.schedule.basic.domain.BasicWorkSchedule;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkCreationDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkDeleteDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkReadDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkUpdateDTO;
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
@RequestMapping("/schedule/basic")
@Slf4j
@RequiredArgsConstructor
public class BasicWorkScheduleController {

    private final BasicWorkScheduleService basicWorkScheduleService;

    @Operation(summary = "기본 근무 스케줄 생성",
            description = "매장의 기본 근무 스케줄을 생성합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "기본 근무 스케줄 생성 성공",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(value = """
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
                                            """)}))
            },
            deprecated = true
    )
    @PostMapping("")
    @Deprecated
    public ResponseEntity<List<BasicWorkSchedule>> createBasicWorkSchedule(@RequestBody BasicWorkCreationDTO dto) {
        log.info("createBasicWorkSchedule");
        return ResponseEntity.ok(basicWorkScheduleService.createBasicWorkSchedule(dto));
    }

    @Operation(summary = "기본 근무 스케줄 조회",
            description = "매장의 기본 근무 스케줄을 조회합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "기본 근무 스케줄 조회 성공",
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
                                            ] // 리스트 형태로 반환
                                            """)}))
            }
    )
    @GetMapping("")
    public ResponseEntity<List<BasicWorkSchedule>> findAllBasicWorkScheduleByStoreId(@RequestBody BasicWorkReadDTO dto) {
        log.info("findAllBasicWorkScheduleByStoreId");
        return ResponseEntity.ok(basicWorkScheduleService.findAllBasicWorkSchedule(dto));
    }

    @Operation(summary = "기본 근무 스케줄 수정",
            description = "매장의 기본 근무 스케줄을 수정합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "기본 근무 스케줄 수정 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BasicWorkSchedule.class)))
            }
    )
    @PatchMapping("")
    public ResponseEntity<BasicWorkSchedule> updateBasicWorkSchedule(@RequestBody BasicWorkUpdateDTO dto) {
        log.info("updateBasicWorkSchedule");
        return ResponseEntity.ok(basicWorkScheduleService.updateBasicWorkSchedule(dto));
    }

    @Operation(summary = "기본 근무 스케줄 삭제",
            description = "매장의 기본 근무 스케줄을 삭제합니다.",
            tags = {"schedule"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "기본 근무 스케줄 삭제 성공")
            }
    )
    @DeleteMapping("")
    public ResponseEntity<?> deleteBasicWorkSchedule(@RequestBody BasicWorkDeleteDTO dto) {
        log.info("deleteBasicWorkSchedule");
        basicWorkScheduleService.deleteBasicWorkSchedule(dto);
        return ResponseEntity.ok().build();
    }

}
