package com.ss6051.backendspring.store;

import com.ss6051.backendspring.global.exception.UnauthorizedException;
import com.ss6051.backendspring.global.tool.JwtTokenProvider;
import com.ss6051.backendspring.store.domain.Store;
import com.ss6051.backendspring.store.dto.RegisterStoreDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/store")
@Slf4j
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

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
                                                    }""")})),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청; id 파싱 실패 or 존재하지 않는 사용자 ID",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "Long 파싱 실패", value = """
                                                    {
                                                      "message": "Invalid account ID. For input string: a123bd2"
                                                    }"""),
                                            @ExampleObject(name = "사용자 ID 존재하지 않음", value = """
                                                    {
                                                      "message": "해당 ID에 해당하는 사용자를 찾을 수 없음"
                                                    }""")
                                    }))
            })
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(@ModelAttribute RegisterStoreDto registerStoreDto) {
        log.info("registerStore() start");

        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        Store store;
        try {
            store = storeService.registerStore(accountId, registerStoreDto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

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
                                                    }""")})),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청; 존재하지 않는 매장 ID",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "해당 ID에 해당하는 매장을 찾을 수 없음", value = """
                                                    {
                                                      "message": "해당 ID에 해당하는 매장을 찾을 수 없음"
                                                    }""")
                                    }))
            })
    @PostMapping("/find")
    public ResponseEntity<?> findStore(@RequestParam("storeId") long storeId) {
        log.info("findStore() start");
        ResponseEntity<?> ret = storeService.findStore(storeId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 ID에 해당하는 매장을 찾을 수 없음"));
        log.info("findStore() end");
        return ret;
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
                                    })),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청; 존재하지 않는 사용자 ID 또는 매장 ID",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "해당 ID에 해당하는 사용자를 찾을 수 없음", value = """
                                                    {
                                                      "message": "해당 ID에 해당하는 사용자를 찾을 수 없음"
                                                    }"""),
                                            @ExampleObject(name = "매장 정보를 찾을 수 없음", value = """
                                                    {
                                                      "message": "매장 정보를 찾을 수 없음"
                                                    }""")
                                    })),
                    @ApiResponse(responseCode = "403", description = "권한 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "해당 매장의 관리자가 아님", value = """
                                                    {
                                                      "message": "해당 매장의 관리자가 아님"
                                                    }""")
                                    }))
            })
    @PostMapping("/generateCode")
    public ResponseEntity<String> generateCode(@RequestParam("storeId") long storeId) {
        log.info("generateCode() start");

        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        String generatedCode;
        try {
            generatedCode = storeService.generateCode(accountId, storeId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }

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
                    @ApiResponse(responseCode = "200", description = "직원 등록 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청; 존재하지 않는 사용자 ID 또는 매장 ID 또는 일회성 코드 입력 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "Long 파싱 실패", value = """
                                                    {
                                                      "message": "Invalid account ID. For input string: a123bd2"
                                                    }"""),
                                            @ExampleObject(name = "사용자 ID 존재하지 않음", value = """
                                                    {
                                                      "message": "해당 ID에 해당하는 사용자를 찾을 수 없음"
                                                    }"""),
                                            @ExampleObject(name = "잘못된 코드 입력", value = """
                                                    {
                                                      "message": "입력한 코드에 해당하는 매장 ID를 찾을 수 없음"
                                                    }""")
                                    })),
                    @ApiResponse(responseCode = "422", description = "이미 매장에 등록된 직원",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "이미 매장에 소속된 직원", value = """
                                                    {
                                                      "message": "이미 매장에 소속된 직원"
                                                    }""")
                                    }))
            })
    @PostMapping("/registerEmployee")
    public ResponseEntity<?> registerEmployee(@RequestParam("code") String code) {
        log.info("registerEmployee() start");
        // 쿠키의 accountId 값으로 매장 ID를 조회해야 하므로 파라미터로 쿠키의 회원 아이디 넘기기
        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        try {
            storeService.registerEmployee(accountId, code);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }

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
                    @ApiResponse(responseCode = "400", description = "잘못된 요청; 존재하지 않는 사용자 ID 또는 매장 ID",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "해당 ID에 해당하는 사용자를 찾을 수 없음", value = """
                                                    {
                                                      "message": "해당 ID에 해당하는 사용자를 찾을 수 없음"
                                                    }"""),
                                            @ExampleObject(name = "매장 정보를 찾을 수 없음", value = """
                                                    {
                                                      "message": "매장 정보를 찾을 수 없음"
                                                    }""")
                                    })),
                    @ApiResponse(responseCode = "403", description = "권한 없음; 관리자가 아니거나 해당 매장 소속이 아님",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name = "해당 매장의 관리자가 아님", value = """
                                                    {
                                                      "message": "해당 매장의 관리자가 아님"
                                                    }"""),
                                            @ExampleObject(name = "해당 매장의 소속이 아님", value = """
                                                    {
                                                      "message": "해당 매장의 소속이 아님"
                                                    }""")
                                    }))
            })
    @PostMapping("/setRole")
    public ResponseEntity<?> setRole(@RequestParam("storeId") long storeId, @RequestParam("role") String role) {
        log.info("setRole() start");

        long accountId = JwtTokenProvider.getAccountIdFromSecurity();

        try {
            storeService.updateRole(accountId, storeId, role);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }

        log.info("setRole() end: accountId={}, role={}", accountId, role);
        return ResponseEntity.ok().build();
    }
}
