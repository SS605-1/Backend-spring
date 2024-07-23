package com.ss6051.backendspring.service;

import com.ss6051.backendspring.domain.Account;
import com.ss6051.backendspring.domain.Address;
import com.ss6051.backendspring.domain.Store;
import com.ss6051.backendspring.dto.RegisterStoreDto;
import com.ss6051.backendspring.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final AuthService authService;
    private final StoreRepository storeRepository;


    /**
     * 매장 정보를 등록한다.
     * @param accountId 신규 매장을 등록하는 사장의 ID 번호
     * @param registerStoreDto 등록할 매장 정보
     * @return {@code ResponseEntity<Store>} 매장 등록 성공 시 매장 정보를 담은 ResponseEntity. 실패 시 bad request
     *
     * @see RegisterStoreDto
     */
    public ResponseEntity<Store> registerStore(long accountId, RegisterStoreDto registerStoreDto) {
        // accountId 조회
        Optional<Account> account = authService.findAccount(accountId);

        // account가 존재하지 않으면 bad request
        if (account.isEmpty()) {
            log.info("신규 매장 등록 중지됨 - 주어진 ID에 해당하는 계정을 찾지 못함: accountId={}", accountId);
            return ResponseEntity.badRequest().build();
        }

        // account가 존재하면 매장 정보 등록
        Store newStore = Store.builder()
                .boss(account.get())
                .name(registerStoreDto.getStore_name())
                .address(new Address(registerStoreDto.street_address, registerStoreDto.lot_number_address))
                .build();
        log.info("신규 매장 등록: store={}", newStore);
        storeRepository.save(newStore);

        return ResponseEntity.ok().body(newStore);
    }

    /**
     * 매장 정보를 조회한다.
     * @param storeId 조회할 매장의 ID 번호
     * @return Optional<Store> 매장 정보를 담은 Optional. 매장이 존재하지 않으면 empty
     */
    public Optional<Store> findStore(long storeId) {
        return storeRepository.findById(storeId);
    }
}
