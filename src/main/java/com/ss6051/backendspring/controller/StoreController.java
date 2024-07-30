package com.ss6051.backendspring.controller;

import com.ss6051.backendspring.aop.ThreadLocalCookieContext;
import com.ss6051.backendspring.dto.RegisterStoreDto;
import com.ss6051.backendspring.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/store")
@Slf4j
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * 매장 정보를 등록한다.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(@ModelAttribute RegisterStoreDto registerStoreDto) {
        log.info("registerStore() start");
        // 매장 정보를 등록하는 로직
        long accountId;
        try { // 올바른 accountId 값이 들어왔는지 확인
            accountId = Long.parseLong(ThreadLocalCookieContext.getCookieValue());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        ResponseEntity<?> ret = storeService.registerStore(accountId, registerStoreDto);
        log.info("registerStore() end");
        return ret;
    }

    /**
     * 매장 정보를 조회한다.
     * @param storeId 조회할 매장의 ID 번호
     * @return ResponseEntity<Store> 매장 정보를 담은 ResponseEntity. 매장이 존재하지 않으면 bad request
     */
    @PostMapping("/find")
    public ResponseEntity<?> findStore(@RequestParam("storeId") long storeId) {
        log.info("findStore() start");
        ResponseEntity<?> ret = storeService.findStore(storeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
        log.info("findStore() end");
        return ret;
    }

    /**
     * 일회성 코드를 생성한다.
     * @param storeId 일회성 코드를 생성할 매장의 ID 번호
     * @return ResponseEntity<?> 일회성 코드 생성 결과
     */
    @PostMapping("/generateCode")
    public ResponseEntity<?> generateCode(@RequestParam("storeId") long storeId){
        log.info("generateCode() start");
        // 쿠키의 accountId 값으로 매장 ID를 조회해야 하므로 파라미터로 쿠키의 회원 아이디 넘기기
        long accountId;
        try { // 올바른 accountId 값이 들어왔는지 확인
            accountId = Long.parseLong(ThreadLocalCookieContext.getCookieValue());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid account ID");
        }
        ResponseEntity<?> ret = storeService.generateCode(accountId, storeId);
        log.info("generateCode() end");
        return ret;
    }

    /**
     * 일회성 코드를 입력해 매장의 직원으로 등록한다.
     * @param code 일회성 코드
     *             매장 ID는 쿠키의 accountId 값으로 조회한다.
     * @return ResponseEntity<?> 매장 직원 등록 결과
     */
    @PostMapping("/registerEmployee")
    public ResponseEntity<?> registerEmployee(@RequestParam("code") String code) {
        log.info("registerEmployee() start");
        // 쿠키의 accountId 값으로 매장 ID를 조회해야 하므로 파라미터로 쿠키의 회원 아이디 넘기기
        long accountId;
        try { // 올바른 accountId 값이 들어왔는지 확인
            accountId = Long.parseLong(ThreadLocalCookieContext.getCookieValue());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid account ID");
        }
        ResponseEntity<?> ret = storeService.registerEmployee(accountId, code);
        log.info("registerEmployee() end");
        return ret;
    }

    /**
     * 권한 설정
     * @param storeId 매장 ID
     * @param role 권한 레벨: BOSS, MANAGER, EMPLOYEE
     * @return {@code ResponseEntity<LoginResponseDto>} 권한 레벨이 변경된 사용자 정보를 담은 ResponseEntity. 실패 시 빈 ResponseEntity
     */
    @PostMapping("/setRole")
    public ResponseEntity<?> setRole(@RequestParam("storeId") long storeId , @RequestParam("role") String role) {
        log.info("setRole() start");

        long accountId;
        try { // 올바른 accountId 값이 들어왔는지 확인
            accountId = Long.parseLong(ThreadLocalCookieContext.getCookieValue());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid account ID");
        }

        ResponseEntity<?> ret = storeService.updateRole(accountId, storeId, role);
        log.info("setRole() end: accountId={}, role={}", accountId, role);
        return ret;
    }
}
