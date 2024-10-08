package com.ss6051.backendspring.store;


import com.ss6051.backendspring.account.AccountService;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.domain.Role;
import com.ss6051.backendspring.global.exception.CustomException;
import com.ss6051.backendspring.global.exception.ErrorCode;
import com.ss6051.backendspring.salary.dto.SalaryCalculateDTO;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.store.domain.Address;
import com.ss6051.backendspring.store.domain.Store;
import com.ss6051.backendspring.store.domain.StoreAccount;
import com.ss6051.backendspring.store.dto.AllAccountsRequestDTO;
import com.ss6051.backendspring.store.dto.RegisterStoreDto;
import com.ss6051.backendspring.store.repository.AddressRepository;
import com.ss6051.backendspring.store.repository.StoreAccountRepository;
import com.ss6051.backendspring.store.repository.StoreRepository;
import com.ss6051.backendspring.store.tool.OneTimeCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreAccountRepository storeAccountRepository;
    private final AddressRepository addressRepository;

    private final AccountService accountService;

    private final OneTimeCodeGenerator oneTimeCodeGenerator;

    /**
     * 해당 매장에 대한 관리자 권한도 없고 사장도 아닌가?
     *
     * @param storeId 매장 ID
     * @param account 계정 정보
     */
    private void checkPermission(long storeId, Account account) {
        List<String> authoritiesToString = account.getAuthoritiesToString();
        if (!(authoritiesToString.contains("STORE_" + storeId + "_ROLE_OWNER") ||
                authoritiesToString.contains("STORE_" + storeId + "_ROLE_MANAGER")))
            throw new CustomException(ErrorCode.ROLE_ACCESS_DENIED);
    }

    public boolean checkPermission(long storeId, long accountId) {
        try {
            checkPermission(storeId, accountService.findAccount(accountId));
            return true;
        } catch (CustomException e) {
            return false;
        }
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
        Account account = accountService.findAccount(accountId);

        // 주소 중복 체크
        boolean isExistAddress = addressRepository.existsByStreetAddressOrLotNumberAddress(registerStoreDto.streetAddress, registerStoreDto.lotNumberAddress);
        if (isExistAddress) {
            log.error("registerStore() error: address already exists");
            throw new CustomException(ErrorCode.ADDRESS_ALREADY_EXISTS);
        }

        // 주소 저장
        Address address = addressRepository.save(Address.builder()
                .streetAddress(registerStoreDto.streetAddress)
                .lotNumberAddress(registerStoreDto.lotNumberAddress)
                .build());

        // 매장 저장
        Store newStore = Store.builder()
                .owner(account)
                .name(registerStoreDto.getStoreName())
                .address(address)
//                .schedule(null)
                .build();

        storeRepository.save(newStore);
        storeAccountRepository.save(StoreAccount.builder()
                .store(newStore)
                .account(account)
                .role(Role.OWNER)
                .build());

        return newStore;
    }

    @Transactional
    public Store setSchedule(Store newStore, Schedule schedule) {
        return newStore;
//        newStore.setSchedule(schedule);
//        return storeRepository.save(newStore);
    }

    /**
     * 매장 정보를 조회한다.
     *
     * @param storeId 조회할 매장의 ID 번호
     * @return Optional<Store> 매장 정보를 담은 Optional. 매장이 존재하지 않으면 empty
     */
    @Transactional(readOnly = true)
    public Store findStore(Long storeId) {
        Optional<Store> byId = storeRepository.findById(storeId);
        if (byId.isEmpty()) {
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
     * 일회성 코드를 입력해 매장의 직원으로 등록한다.
     *
     * @param accountId 직원으로 등록할 계정 ID
     * @param code      일회성 코드
     */
    @Transactional
    public Long registerEmployee(long accountId, String code) {
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
        storeAccountRepository.save(StoreAccount.builder()
                .store(store)
                .account(account)
                .role(Role.EMPLOYEE)
                .build());
        log.info("직원 등록: accountId={}, storeId={}", accountId, storeId);
        return storeId;
    }

    @Transactional
    public void deleteEmployee(long accountId, long storeId) {
        log.info("직원 삭제 시작: accountId={}, storeId={}", accountId, storeId);
        Account account = accountService.findAccount(accountId);
        Store store = findStore(storeId);

        if (store.getOwner().equals(account)) {
            log.info("직원 삭제 중지됨 - 사장은 삭제할 수 없음: accountId={}, storeId={}", accountId, storeId);
            throw new CustomException(ErrorCode.ROLE_ACCESS_DENIED);
        }

        // 매장에 소속된 직원이 아니면 bad request
        if (!store.getAllAccounts().contains(account)) {
            log.info("직원 삭제 중지됨 - 매장에 소속되지 않은 직원: accountId={}, storeId={}", accountId, storeId);
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        store.removeAccount(account);
        storeRepository.save(store);
        storeAccountRepository.findByStoreIdAndAccountId(storeId, accountId).ifPresent(storeAccountRepository::delete);
        log.info("직원 삭제: accountId={}, storeId={}", accountId, storeId);
    }

    /**
     * 권한 변경
     *
     * @param accountId db에 반영되어 있는 사용자 id 값
     * @param storeId   db에 반영되어 있는 매장 id 값
     * @param role      권한 레벨
     * @see Role
     */
    @Transactional
    public void updateRole(Long accountId, long storeId, String role) {
        StoreAccount storeAccount = getAccount(accountId, storeId);

        try {
            /* 이 부분은 프론트에서 실제 직원 권한 변경 때 막아두는게 좋을듯.
            if (Role.valueOf(role) == Role.OWNER) {
                throw new CustomException(ErrorCode.ROLE_ACCESS_DENIED, "사장 권한은 변경할 수 없습니다");
            }
            */
            storeAccount.setRole(Role.valueOf(role));

            storeAccountRepository.save(storeAccount);
            log.info("권한 변경: accountId={}, storeId={}, role={}", accountId, storeId, role);

        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 권한입니다");
        }

    }

    @Transactional(readOnly = true)
    public List<AllAccountsRequestDTO> getAllAccounts(Long storeId) {
        Store store = findStore(storeId);
        Stream<Account> allAccounts = store.getEmployeeList().stream().map(StoreAccount::getAccount);

        return allAccounts
                .map(account -> new AllAccountsRequestDTO(account.getId(), account.getNickname()))
                .toList();
    }

    /**
     * 기본급 변경
     *
     * @param accountId  db에 반영되어 있는 사용자 id 값
     * @param storeId    db에 반영되어 있는 매장 id 값
     * @param baseSalary 변경할 기본급
     */
    @Transactional
    public void setBaseSalary(Long accountId, long storeId, long baseSalary) {
        StoreAccount storeAccount = getAccount(accountId, storeId);
        if (baseSalary < 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "기본급이 0보다 작습니다");
        }

        storeAccount.setBaseSalary(baseSalary);
        storeAccountRepository.save(storeAccount);
        log.info("기본 급여 변경: accountId={}, storeId={}, baseSalary={}", accountId, storeId, baseSalary);
    }

    @Transactional(readOnly = true)
    public Long getBaseSalary(Long accountId, long storeId) {
        StoreAccount storeAccount = getAccount(accountId, storeId);
        return storeAccount.getBaseSalary();
    }

    @Transactional(readOnly = true)
    public StoreAccount getAccount(Long accountId, long storeId) {
        Account account = accountService.findAccount(accountId);
        findStore(storeId);
        checkPermission(storeId, account);

        Optional<StoreAccount> byStoreIdAndAccountId = storeAccountRepository.findByStoreIdAndAccountId(storeId, accountId);
        if (byStoreIdAndAccountId.isEmpty()) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return byStoreIdAndAccountId.get();
    }

    @Transactional(readOnly = true)
    public List<Long> findAllByAccountId(Long accountId) {
        List<StoreAccount> allByAccountId = storeAccountRepository.findAllByAccountId(accountId);
        log.info("회원 ID로 조회한 매장 ID 목록: accountId={}, storeId={}", accountId, allByAccountId);
        return allByAccountId.stream().map(storeAccount ->
                storeAccount.getStore().getId()).toList();
    }

    @Transactional(readOnly = true)
    public List<String> findAllStoreNameByAccountId(Long accountId) {
        List<StoreAccount> allByAccountId = storeAccountRepository.findAllByAccountId(accountId);
        log.info("회원 ID로 조회한 매장 ID 목록: accountId={}, storeId={}", accountId, allByAccountId);
        return allByAccountId.stream().map(storeAccount ->
                storeAccount.getStore().getName()).toList();
    }


}
