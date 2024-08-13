package com.ss6051.backendspring.store;


import com.ss6051.backendspring.account.AccountService;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.domain.Role;
import com.ss6051.backendspring.global.exception.UnauthorizedException;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.store.domain.Address;
import com.ss6051.backendspring.store.domain.Store;
import com.ss6051.backendspring.store.domain.StoreAccount;
import com.ss6051.backendspring.store.dto.RegisterStoreDto;
import com.ss6051.backendspring.store.repository.StoreAccountRepository;
import com.ss6051.backendspring.store.repository.StoreRepository;
import com.ss6051.backendspring.store.tool.OneTimeCodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 해당 매장에 대한 관리자 권한도 없고 사장도 아닌가?
     *
     * @param storeId 매장 ID
     * @param account 계정 정보
     * @return true: 권한이 없음, false: 권한이 있음
     */
    private static boolean hasNoPermission(long storeId, Account account) {
        return !(account.getAuthoritiesToString().contains("STORE_" + storeId + "_ROLE_OWNER") ||
                account.getAuthoritiesToString().contains("STORE_" + storeId + "_ROLE_MANAGER"));
    }

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
        Optional<Account> account = accountService.findAccount(accountId);

        // account 가 존재하지 않으면 bad request
        if (account.isEmpty()) {
            throw new IllegalArgumentException("해당 ID에 해당하는 사용자를 찾을 수 없음");
        }

        // account 가 존재하면 매장 정보 등록
        Store newStore = Store.builder()
                .owner(account.get())
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
     *
     * @param storeId 조회할 매장의 ID 번호
     * @return Optional<Store> 매장 정보를 담은 Optional. 매장이 존재하지 않으면 empty
     */
    @Transactional(readOnly = true)
    public Optional<Store> findStore(long storeId) {
        return storeRepository.findById(storeId);
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
        Optional<Account> accountOpt = accountService.findAccount(accountId);
        // 회원 정보를 조회해 없는 회원이면 bad request
        if (accountOpt.isEmpty()) {
            log.info("일회성 코드 생성 중지됨 - 주어진 ID에 해당하는 계정을 찾지 못함: accountId={}", accountId);
            throw new EntityNotFoundException("해당 ID에 해당하는 사용자를 찾을 수 없음");
        }
        Account account = accountOpt.get();

        // 매장 정보가 없으면 bad request
        Optional<Store> store = findStore(storeId);
        if (store.isEmpty()) {
            log.info("일회성 코드 생성 중지됨 - 주어진 ID에 해당하는 매장을 찾지 못함: storeId={}", storeId);
            throw new EntityNotFoundException("매장 정보를 찾을 수 없음");
        }

        // 매장에 소속된 관리 권한을 가진 유저가 아니면 bad request
        if (hasNoPermission(storeId, account)) {
            log.info("일회성 코드 생성 중지됨 - 주어진 ID에 해당하는 계정이 매장의 관리자가 아님: accountId={}, storeId={}", accountId, storeId);
            throw new UnauthorizedException("해당 매장의 관리자가 아님");
        }

        // 일회성 코드 생성
        return oneTimeCodeGenerator.generateUniqueCode(storeId);
    }

    /**
     * 일회성 코드를 입력해 매장의 직원으로 등록한다.
     *
     * @param accountId 직원으로 등록할 계정 ID
     * @param code      일회성 코드
     * @return
     */
    @Transactional
    public Store registerEmployee(long accountId, String code) {
        Optional<Account> account = accountService.findAccount(accountId);
        // 회원 정보를 조회해 없는 회원이면 bad request
        if (account.isEmpty()) {
            log.info("직원 등록 중지됨 - 주어진 ID에 해당하는 계정을 찾지 못함: accountId={}", accountId);
            throw new EntityNotFoundException("해당 ID에 해당하는 사용자를 찾을 수 없음");
        }

        // 일회성 코드에 해당하는 매장 ID 조회 - 맞는 코드가 없으면 bad request
        Long storeId = oneTimeCodeGenerator.getStoreIdWithCode(code);
        if (storeId == null) {
            log.info("직원 등록 중지됨 - 주어진 코드에 해당하는 매장 ID를 찾지 못함: code={}", code);
            throw new EntityNotFoundException("입력한 코드에 해당하는 매장 ID를 찾을 수 없음");
        }

        // 매장 정보가 없으면 bad request
        Optional<Store> store = findStore(storeId);
        if (store.isEmpty()) {
            log.info("직원 등록 중지됨 - 주어진 ID에 해당하는 매장을 찾지 못함: storeId={}", storeId);
            throw new EntityNotFoundException("매장 정보를 찾을 수 없음");
        }

        // 이미 매장에 소속된 직원이면 bad request
        if (store.get().getAllAccounts().contains(account.get())) {
            log.info("직원 등록 중지됨 - 이미 매장에 소속된 직원: accountId={}, storeId={}", accountId, storeId);
            throw new IllegalStateException("이미 매장에 소속된 직원");
        }

        store.get().addEmployee(account.get());
        Store save = storeRepository.save(store.get());
        log.info("직원 등록: accountId={}, storeId={}", accountId, storeId);
        return save;
    }

    /**
     * 권한 변경
     *
     * @param accountId db에 반영되어 있는 사용자 id 값
     * @param storeId   db에 반영되어 있는 매장 id 값
     * @param role      권한 레벨
     * @return
     * @see Role
     */
    @Transactional
    public StoreAccount updateRole(Long accountId, long storeId, String role) {
        Optional<Account> accountOpt = accountService.findAccount(accountId);
        if (accountOpt.isEmpty()) {
            throw new EntityNotFoundException("해당 ID에 해당하는 사용자를 찾을 수 없음");
        }
        Account account = accountOpt.get();

        Optional<Store> findStore = findStore(storeId);
        if (findStore.isEmpty()) {
            throw new EntityNotFoundException("해당 ID에 해당하는 매장을 찾을 수 없음");
        }

        // 매장에 소속된 관리 권한을 가진 유저가 아니면 bad request
        if (hasNoPermission(storeId, account)) {
            throw new UnauthorizedException("해당 매장의 관리자가 아님");
        }

        Optional<StoreAccount> byStoreIdAndAccountId = storeAccountRepository.findByStoreIdAndAccountId(storeId, accountId);
        if (byStoreIdAndAccountId.isEmpty()) {
            throw new UnauthorizedException("해당 매장에 소속되어 있지 않음");
        }

        StoreAccount storeAccount = byStoreIdAndAccountId.get();
        storeAccount.setRole(Role.valueOf(role));
        StoreAccount save = storeAccountRepository.save(storeAccount);
        log.info("권한 변경: accountId={}, storeId={}, role={}", accountId, storeId, role);
        return save;
    }
}
