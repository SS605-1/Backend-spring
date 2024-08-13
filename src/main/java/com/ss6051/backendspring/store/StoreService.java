package com.ss6051.backendspring.store;


import com.ss6051.backendspring.account.AccountService;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.domain.Role;
import com.ss6051.backendspring.global.exception.CustomException;
import com.ss6051.backendspring.global.exception.ErrorCode;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.store.domain.Address;
import com.ss6051.backendspring.store.domain.Store;
import com.ss6051.backendspring.store.domain.StoreAccount;
import com.ss6051.backendspring.store.dto.RegisterStoreDto;
import com.ss6051.backendspring.store.repository.StoreAccountRepository;
import com.ss6051.backendspring.store.repository.StoreRepository;
import com.ss6051.backendspring.store.tool.OneTimeCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;

    private final AccountService accountService;

    private final OneTimeCodeGenerator oneTimeCodeGenerator;

    /**
     * 매장 정보를 등록한다.
     *
     * @param accountId        신규 매장을 등록하는 사장의 ID 번호
     * @param registerStoreDto 등록할 매장 정보
     * @return {@code Store} 매장 정보
     * @see RegisterStoreDto
     */
    @Transactional
    public Store registerStore(long accountId, RegisterStoreDto registerStoreDto) {
        // accountId 조회
        Account account = accountService.findAccount(accountId);

        // account 가 존재하면 매장 정보 등록
        Store newStore = Store.builder()
                .owner(account)
                .name(registerStoreDto.getStore_name())
                .address(new Address(registerStoreDto.street_address, registerStoreDto.lot_number_address))
                .schedule(null)
                .build();
        log.info("신규 매장 등록: store={}", newStore);
        storeRepository.save(newStore);

        return newStore;
    }

    @Transactional
    public Store setSchedule(Store newStore, Schedule schedule) {
        newStore.setSchedule(schedule);
        return storeRepository.save(newStore);
    }

    /**
     * 매장 정보를 조회한다.
     * @param storeId 조회할 매장의 ID 번호
     * @return Optional<Store> 매장 정보를 담은 Optional. 매장이 존재하지 않으면 empty
     */
    @Transactional(readOnly = true)
    public Store findStore(Long storeId) {
        Optional<Store> byId = storeRepository.findById(storeId);
        if (byId.isEmpty()) {
            log.error("findStore() error: entity not found by storeId={}", storeId);
            throw new CustomException(ErrorCode.STORE_NOT_FOUND, storeId.toString());
        }
        return byId.get();
    }

    /**
     * 일회성 코드를 생성한다.
     *
     * @param accountId 일회성 코드를 생성할 계정 ID
     * @param storeId   일회성 코드를 생성할 매장 ID
     * @return {@code code} 일회성 코드 생성 결과
     */
    @Transactional(readOnly = true)
    public String generateCode(long accountId, long storeId) {
        log.info("일회성 코드 생성 시작: accountId={}, storeId={}", accountId, storeId);

        // 각종 예외 처리
        Account account = accountService.findAccount(accountId);
        findStore(storeId);
        checkPermission(storeId, account);

        // 일회성 코드 생성
        String code = oneTimeCodeGenerator.generateUniqueCode(storeId);
        log.info("일회성 코드 생성 완료: accountId={}, storeId={}, code={}", accountId, storeId, code);
        return code;
    }

    /**
     * 해당 매장에 대한 관리자 권한도 없고 사장도 아닌가?
     * @param storeId 매장 ID
     * @param account 계정 정보
     */
    private static void checkPermission(long storeId, Account account) {
        List<String> authoritiesToString = account.getAuthoritiesToString();
        if  (!(authoritiesToString.contains("STORE_" + storeId + "_ROLE_OWNER") ||
                authoritiesToString.contains("STORE_" + storeId + "_ROLE_MANAGER")))
            throw new CustomException(ErrorCode.ROLE_ACCESS_DENIED);
    }

    /**
     * 일회성 코드를 입력해 매장의 직원으로 등록한다.
     *
     * @param accountId 직원으로 등록할 계정 ID
     * @param code      일회성 코드
     */
    @Transactional
    public void registerEmployee(long accountId, String code) {
        log.info("직원 등록 시작: accountId={}, code={}", accountId, code);
        Account account = accountService.findAccount(accountId);

        // 일회성 코드에 해당하는 매장 ID 조회 - 맞는 코드가 없으면 bad request
        Long storeId = oneTimeCodeGenerator.getStoreIdWithCode(code);

        // 매장 정보가 없으면 bad request
        Store store = findStore(storeId);

        // 이미 매장에 소속된 직원이면 bad request
        if (store.getAllAccounts().contains(account)) {
            log.info("직원 등록 중지됨 - 이미 매장에 소속된 직원: accountId={}, storeId={}", accountId, storeId);
            throw new CustomException(ErrorCode.STORE_ALREADY_REGISTERED_MEMBER);
        }

        store.addEmployee(account);
        storeRepository.save(store);
        log.info("직원 등록: accountId={}, storeId={}", accountId, storeId);
    }

    /**
     * 권한 변경
     *
     * @param accountId      db에 반영되어 있는 사용자 id 값
     * @param storeId      db에 반영되어 있는 매장 id 값
     * @param role    권한 레벨
     * @see Role
     */
    @Transactional
    public void updateRole(Long accountId, long storeId, String role) {
        Account account = accountService.findAccount(accountId); // 사용자 정보 조회
        findStore(storeId); // 매장 정보 존재 유무 체크; 예외처리는 호출한 메소드에서 함.

        checkPermission(storeId, account);

        Optional<StoreAccount> byStoreIdAndAccountId = storeAccountRepository.findByStoreIdAndAccountId(storeId, accountId);
        if (byStoreIdAndAccountId.isEmpty()) {
            throw new CustomException(ErrorCode.ROLE_ACCESS_DENIED);
        }

        StoreAccount storeAccount = byStoreIdAndAccountId.get();
        storeAccount.setRole(Role.valueOf(role));
        storeAccountRepository.save(storeAccount);
        log.info("권한 변경: accountId={}, storeId={}, role={}", accountId, storeId, role);
    }
}
