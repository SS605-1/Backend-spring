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
}
