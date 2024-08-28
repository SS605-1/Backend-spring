package com.ss6051.backendspring.store;

import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.tool.JwtTokenProvider;
import com.ss6051.backendspring.schedule.common.ScheduleService;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.store.domain.Store;
import com.ss6051.backendspring.store.dto.RegisterStoreDto;
import com.ss6051.backendspring.store.dto.StoreNameAddrDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<?> registerStore(@RequestBody RegisterStoreDto registerStoreDto) {
        log.info("registerStore() start");

        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        Store store1 = storeService.registerStore(accountId, registerStoreDto);
        Schedule schedule = scheduleService.createSchedule(store1);
        Store store = storeService.setSchedule(store1, schedule);
        log.info("신규 매장 등록: store={}", store);
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

        Long storeId = storeService.registerEmployee(accountId, code);

        log.info("registerEmployee() end");
        return ResponseEntity.ok(storeId);
    }

    @Operation(summary = "직원 삭제",
            description = "매장의 직원을 삭제합니다.",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "직원 삭제 성공")
            })
    @DeleteMapping("/delete/employee")
    public ResponseEntity<?> deleteEmployee(@RequestParam("employeeId") long employeeId) {
        log.info("deleteEmployee() start");

        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        storeService.deleteEmployee(accountId, employeeId);

        log.info("deleteEmployee() end");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "매장 전체 직원 조회",
            description = "매장에 속한 모든 직원을 조회합니다.",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "매장 삭제 성공")
            })
    @GetMapping("/user/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        long accountId = JwtTokenProvider.getAccountIdFromSecurity();
        List<Account> allAccounts = storeService.getAllAccounts(accountId);
        return ResponseEntity.ok(allAccounts);
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

    @Operation(summary = "기본급 설정",
            description = "직원의 기본급을 설정합니다. 0 이상의 양수로 설정해야 합니다.",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "기본급 설정 성공"),
            })
    @PostMapping("/salary")
    public ResponseEntity<?> setBaseSalary(@RequestParam("employeeId") long employeeId, @RequestParam("salary") long salary) {
        long accountId = JwtTokenProvider.getAccountIdFromSecurity();
        storeService.setBaseSalary(accountId, employeeId, salary);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/salary")
    public ResponseEntity<?> getBaseSalary(@RequestParam("employeeId") long employeeId) {
        long accountId = JwtTokenProvider.getAccountIdFromSecurity();
        long salary = storeService.getBaseSalary(accountId, employeeId);
        return ResponseEntity.ok(salary);
    }

    @Operation(summary = "매장 조회",
            description = "사용자가 등록된 모든 매장을 조회합니다. (pathvariable로 사용자 id를 받아 조회)",
            tags = {"store"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "매장 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class),
                                    examples = {
                                            @ExampleObject(value = """
                                                    [1, 2, 3]""")
                                    }))
            })
    @GetMapping("/user/{accountId}")
    public ResponseEntity<?> getAllAssignedStores(@PathVariable long accountId) {
        List<Long> allByAccountId = storeService.findAllByAccountId(accountId);
        return ResponseEntity.ok(allByAccountId);
    }

    @Operation(summary = "매장 조회",
            description = "사용자가 등록된 모든 매장을 조회합니다. (jwt 토큰 값에서 사용자 id를 파싱해서 조회)",
            tags = {"store"})
    @GetMapping("/user")
    public ResponseEntity<List<StoreNameAddrDTO>> getAllAssignedStores() {
        long accountId = JwtTokenProvider.getAccountIdFromSecurity();
        List<Long> allByAccountId = storeService.findAllByAccountId(accountId);
        List<StoreNameAddrDTO> result = new ArrayList<>();
        allByAccountId.forEach(id -> {
            Store store = storeService.findStore(id);
            result.add(
                    new StoreNameAddrDTO(store.getId(), store.getName(), store.getAddress().getStreetAddress(), store.getAddress().getLotNumberAddress())
            );
        });
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "권한 확인",
            description = "사용자가 매장에 대한 권한을 가지고 있는지 확인합니다.",
            tags = {"store"})
    @GetMapping("/check-permission")
    public ResponseEntity<?> checkPermission(@RequestParam("storeId") long storeId) {
        long accountId = JwtTokenProvider.getAccountIdFromSecurity();
        boolean hasPermission = storeService.checkPermission(storeId, accountId);
        return ResponseEntity.ok(hasPermission);
    }

}
