package com.ss6051.backendspring.service;


import com.ss6051.backendspring.domain.*;
import com.ss6051.backendspring.dto.RegisterStoreDto;
import com.ss6051.backendspring.repository.StoreAccountRepository;
import com.ss6051.backendspring.repository.StoreRepository;
import com.ss6051.backendspring.tool.OneTimeCodeGenerator;
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
    private final StoreAccountRepository storeAccountRepository;
    private final OneTimeCodeGenerator oneTimeCodeGenerator;


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

    /**
     * 일회성 코드를 생성한다.
     * @param accountId 일회성 코드를 생성할 계정 ID
     * @param storeId 일회성 코드를 생성할 매장 ID
     * @return ResponseEntity<?> 일회성 코드 생성 결과
     */
    public ResponseEntity<?> generateCode(long accountId, long storeId) {
        Optional<Account> account = authService.findAccount(accountId);
        // 회원 정보를 조회해 없는 회원이면 bad request
        if (account.isEmpty()) {
            log.info("일회성 코드 생성 중지됨 - 주어진 ID에 해당하는 계정을 찾지 못함: accountId={}", accountId);
            return ResponseEntity.badRequest().body("회원 정보를 찾을 수 없음");
        }

        // 매장 정보가 없으면 bad request
        Optional<Store> store = findStore(storeId);
        if (store.isEmpty()) {
            log.info("일회성 코드 생성 중지됨 - 주어진 ID에 해당하는 매장을 찾지 못함: storeId={}", storeId);
            return ResponseEntity.badRequest().body("매장 정보를 찾을 수 없음");
        }

        // 매장에 소속된 관리 권한을 가진 유저가 아니면 bad request
        if (store.get().isNotManageableAccount(account.get())) {
            log.info("일회성 코드 생성 중지됨 - 주어진 ID에 해당하는 계정이 매장의 관리자가 아님: accountId={}, storeId={}", accountId, storeId);
            return ResponseEntity.badRequest().body("해당 매장의 관리자가 아님");
        }

        // 일회성 코드 생성
        String code = oneTimeCodeGenerator.generateUniqueCode(storeId);

        return ResponseEntity.ok().body(code);
    }

    /**
     * 일회성 코드를 입력해 매장의 직원으로 등록한다.
     * @param accountId 직원으로 등록할 계정 ID
     * @param code 일회성 코드
     * @return ResponseEntity<?> 매장 직원 등록 결과
     */
    public ResponseEntity<?> registerEmployee(long accountId, String code) {
        Optional<Account> account = authService.findAccount(accountId);
        // 회원 정보를 조회해 없는 회원이면 bad request
        if (account.isEmpty()) {
            log.info("직원 등록 중지됨 - 주어진 ID에 해당하는 계정을 찾지 못함: accountId={}", accountId);
            return ResponseEntity.badRequest().body("회원 정보를 찾을 수 없음");
        }

        // 일회성 코드에 해당하는 매장 ID 조회 - 맞는 코드가 없으면 bad request
        Long storeId = oneTimeCodeGenerator.getStoreIdWithCode(code);
        if (storeId == null) {
            log.info("직원 등록 중지됨 - 주어진 코드에 해당하는 매장 ID를 찾지 못함: code={}", code);
            return ResponseEntity.badRequest().body("입력한 코드에 해당하는 매장 ID를 찾을 수 없음");
        }

        // 매장 정보가 없으면 bad request
        Optional<Store> store = findStore(storeId);
        if (store.isEmpty()) {
            log.info("직원 등록 중지됨 - 주어진 ID에 해당하는 매장을 찾지 못함: storeId={}", storeId);
            return ResponseEntity.badRequest().body("매장 정보를 찾을 수 없음");
        }

        // 이미 매장에 소속된 직원이면 bad request
        if (store.get().getAllAccounts().contains(account.get())) {
            log.info("직원 등록 중지됨 - 이미 매장에 소속된 직원: accountId={}, storeId={}", accountId, storeId);
            return ResponseEntity.badRequest().body("이미 매장에 소속된 직원");
        }

        store.get().addEmployee(account.get());
        storeRepository.save(store.get());

        return ResponseEntity.ok().build();
    }

    /**
     * 권한 변경
     *
     * @param accountId      db에 반영되어 있는 사용자 id 값
     * @param storeId      db에 반영되어 있는 매장 id 값
     * @param role    권한 레벨: BOSS, MANAGER, EMPLOYEE
     * @return {@code ResponseEntity<LoginResponseDto>} 권한 레벨이 변경된 사용자 정보를 담은 ResponseEntity. 실패 시 빈 ResponseEntity
     * @see com.ss6051.backendspring.domain.Role
     */
    public ResponseEntity<?> updateRole(Long accountId, long storeId, String role) {
        Account account = authService.findAccount(accountId).orElse(null);
        if (account == null) {
            return ResponseEntity.badRequest().body("해당 ID에 해당하는 사용자를 찾을 수 없음");
        }

        Optional<Store> findStore = findStore(storeId);
        if (findStore.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 ID에 해당하는 매장을 찾을 수 없음");
        }

        // 매장에 소속된 관리 권한을 가진 유저가 아니면 bad request
        Store store = findStore.get();
        if (store.isNotManageableAccount(account)) {
            return ResponseEntity.badRequest().body("해당 매장의 관리자가 아님");
        }

        Optional<StoreAccount> byStoreIdAndAccountId = storeAccountRepository.findByStoreIdAndAccountId(storeId, accountId);
        if (byStoreIdAndAccountId.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 매장에 소속되어 있지 않음");
        }

        StoreAccount storeAccount = byStoreIdAndAccountId.get();
        storeAccount.setRole(Role.valueOf(role));
        storeAccountRepository.save(storeAccount);
        log.info("권한 변경: accountId={}, storeId={}, role={}", accountId, storeId, role);

        return ResponseEntity.ok().build();
    }
}
