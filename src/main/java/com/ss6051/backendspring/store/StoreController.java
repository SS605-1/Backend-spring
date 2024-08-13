package com.ss6051.backendspring.store;

import com.ss6051.backendspring.global.tool.JwtTokenProvider;
import com.ss6051.backendspring.schedule.common.ScheduleService;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.store.domain.Store;
import com.ss6051.backendspring.store.dto.RegisterStoreDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
@Slf4j
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final ScheduleService scheduleService;

    /**
     * 매장 등록
     *
     * @param registerStoreDto 매장 등록 정보
     * @return ResponseEntity<Store> 매장 정보를 담은 ResponseEntity. 실패 시 bad request
     */
    @Operation(summary = "매장 등록",
            description = "매장을 등록합니다. 매장 등록 성공 시 매장 정보를 담은 ResponseEntity를 반환합니다.",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "매장 등록 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Store.class),
                                    examples = {
                                            @ExampleObject(value = """
                                                    {
                                                      "id": 1,
                                                      "name": "매장 이름",
                                                      "address": {
                                                        "street_address": "도로명주소",
                                                        "lot_number_address": "지번주소"
                                                      },
                                                        "owner": {
                                                            "id": 1,
                                                            "nickname": "john_doe",
                                                            "profile_image_url": "https://example.com/profile.jpg",
                                                            "thumbnail_image_url": "https://example.com/thumbnail.jpg"
                                                        },
                                                      "managerList": [
                                                        {
                                                          "account": {
                                                            "id": 1,
                                                            "nickname": "john_doe",
                                                            "profile_image_url": "https://example.com/profile.jpg",
                                                            "thumbnail_image_url": "https://example.com/thumbnail.jpg"
                                                          },
                                                          "role": "OWNER"
                                                        }],
                                                      "employeeList": []
                                                    }""")}))
            })
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(@ModelAttribute RegisterStoreDto registerStoreDto) {
        log.info("registerStore() start");

        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        Store store1 = storeService.registerStore(accountId, registerStoreDto);
        Schedule schedule = scheduleService.createSchedule(store1);
        Store store = storeService.setSchedule(store1, schedule);

        log.info("registerStore() end");
        return ResponseEntity.ok(store);
    }

    /**
     * 매장 정보를 조회한다.
     *
     * @param storeId 조회할 매장의 ID 번호
     * @return ResponseEntity<Store> 매장 정보를 담은 ResponseEntity. 매장이 존재하지 않으면 bad request
     */
    @Operation(summary = "매장 조회",
            description = "매장 정보를 조회합니다. 매장 정보를 담은 ResponseEntity를 반환합니다.",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "매장 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Store.class),
                                    examples = {
                                            @ExampleObject(value = """
                                                    {
                                                      "id": 1,
                                                      "name": "매장 이름",
                                                      "address": {
                                                        "street_address": "도로명주소",
                                                        "lot_number_address": "지번주소"
                                                      },
                                                        "owner": {
                                                            "id": 1,
                                                            "nickname": "john_doe",
                                                            "profile_image_url": "https://example.com/profile.jpg",
                                                            "thumbnail_image_url": "https://example.com/thumbnail.jpg"
                                                        },
                                                      "managerList": [
                                                        {
                                                          "account": {
                                                            "id": 1,
                                                            "nickname": "john_doe",
                                                            "profile_image_url": "https://example.com/profile.jpg",
                                                            "thumbnail_image_url": "https://example.com/thumbnail.jpg"
                                                          },
                                                          "role": "OWNER"
                                                        },
                                                        {
                                                          "account": {
                                                            "id": 2,
                                                            "nickname": "jane_doe",
                                                            "profile_image_url": "https://example.com/profile.jpg",
                                                            "thumbnail_image_url": "https://example.com/thumbnail.jpg"
                                                          },
                                                          "role": "MANAGER"
                                                        }
                                                      ],
                                                      "employeeList": [
                                                        {
                                                          "account": {
                                                            "id": 3,
                                                            "nickname": "another_doe",
                                                            "profile_image_url": "https://example.com/profile.jpg",
                                                            "thumbnail_image_url": "https://example.com/thumbnail.jpg"
                                                          },
                                                          "role": "EMPLOYEE"
                                                        }
                                                      ]
                                                    }""")}))
            })
    @PostMapping("/find")
    public ResponseEntity<?> findStore(@RequestParam("storeId") long storeId) {
        log.info("findStore() start");
        Store store = storeService.findStore(storeId);
        log.info("findStore() end");
        return ResponseEntity.ok(store);
    }

    /**
     * 일회성 코드를 생성한다.
     *
     * @param storeId 일회성 코드를 생성할 매장의 ID 번호
     * @return {@code ResponseEntity<String>} 일회성 코드 생성 결과
     */
    @Operation(summary = "일회성 코드 생성",
            description = "일회성 코드를 생성합니다. 일회성 코드 생성 결과를 담은 ResponseEntity를 반환합니다.",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "일회성 코드 생성 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "일회성 코드 생성 성공", value = """
                                                    {
                                                      "code": "0a1z9"
                                                    }""")
                                    }))
            })
    @PostMapping("/generateCode")
    public ResponseEntity<String> generateCode(@RequestParam("storeId") long storeId) {
        log.info("generateCode() start");

        long accountId = JwtTokenProvider.getAccountIdFromSecurity();
        String generatedCode = storeService.generateCode(accountId, storeId);

        log.info("generateCode() end");
        return ResponseEntity.ok(generatedCode);
    }

    /**
     * 일회성 코드를 입력해 매장의 직원으로 등록한다.
     *
     * @param code 일회성 코드
     *             매장 ID는 쿠키의 accountId 값으로 조회한다.
     * @return ResponseEntity<?> 매장 직원 등록 결과
     */
    @Operation(summary = "직원 등록",
            description = "일회성 코드를 입력해 매장의 직원으로 등록합니다. 매장 직원 등록 결과를 담은 ResponseEntity를 반환합니다.",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "직원 등록 성공")
            })
    @PostMapping("/registerEmployee")
    public ResponseEntity<?> registerEmployee(@RequestParam("code") String code) {
        log.info("registerEmployee() start");
        // 쿠키의 accountId 값으로 매장 ID를 조회해야 하므로 파라미터로 쿠키의 회원 아이디 넘기기
        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        storeService.registerEmployee(accountId, code);

        log.info("registerEmployee() end");
        return ResponseEntity.ok().build();
    }

    /**
     * 권한 설정
     *
     * @param storeId 매장 ID
     * @param role    권한 레벨
     * @return {@code ResponseEntity<LoginResponseDto>} 권한 레벨이 변경된 사용자 정보를 담은 ResponseEntity. 실패 시 빈 ResponseEntity
     */
    @Operation(summary = "권한 설정",
            description = "매장의 직원의 권한을 설정합니다. 권한 설정 결과를 담은 ResponseEntity를 반환합니다.",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "권한 설정 성공"),
            })
    @PostMapping("/setRole")
    public ResponseEntity<?> setRole(@RequestParam("storeId") long storeId, @RequestParam("role") String role) {
        log.info("setRole() start");

        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        storeService.updateRole(accountId, storeId, role);

        log.info("setRole() end: accountId={}, role={}", accountId, role);
        return ResponseEntity.ok().build();
    }
}
