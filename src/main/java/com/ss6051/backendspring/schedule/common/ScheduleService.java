package com.ss6051.backendspring.schedule.common;

import com.ss6051.backendspring.auth.AuthService;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.actual.domain.ActualWorkSchedule;
import com.ss6051.backendspring.schedule.actual.dto.ActualWorkCreationDTO;
import com.ss6051.backendspring.schedule.actual.ActualWorkScheduleRepository;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.schedule.common.domain.ScheduleAccountPair;
import com.ss6051.backendspring.store.StoreService;
import com.ss6051.backendspring.store.domain.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ActualWorkScheduleRepository actualWorkScheduleRepository;

    private final StoreService storeService;
    private final AuthService authService;

    @Transactional
    public Schedule createSchedule(Store store) {
        Schedule schedule = Schedule.builder()
                .storeId(store.getId())
                .store(store)
                .lastModifiedBy(store.getOwner())
                .lastModifiedTime(LocalDateTime.now())
                .build();
        return scheduleRepository.save(schedule);
    }


    @Transactional
    public ActualWorkSchedule createActualWorkSchedule(ActualWorkCreationDTO dto) {

        ScheduleAccountPair pair = getScheduleAndAccount(dto.storeId(), dto.accountId());

        // store 와 account 로 ActualWorkSchedule 생성
        ActualWorkSchedule schedule = ActualWorkSchedule.builder()
                .account(pair.account())
                .schedule(pair.schedule())
                .startDateTime(dto.actualWorkDTO().startDateTime())
                .endDateTime(dto.actualWorkDTO().endDateTime())
                .build();

        return actualWorkScheduleRepository.save(schedule);
    }


    @SuppressWarnings("unused")
    public void deleteSchedule() {
        // Store 가 삭제될 때 CASCADE 로 삭제될 것이므로 별도로 삭제할 필요 없음
    }

    public ScheduleAccountPair getScheduleAndAccount(long storeId, long accountId) {
        // storeId로 store 조회
        Optional<Store> storeOpt = storeService.findStore(storeId);
        if (storeOpt.isEmpty()) {
            throw new IllegalArgumentException("해당 ID에 해당하는 가게를 찾을 수 없음");
        }
        Store store = storeOpt.get();

        // accountId로 account 조회
        Optional<Account> accountOpt = authService.findAccount(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("해당 ID에 해당하는 사용자를 찾을 수 없음");
        }
        Account account = accountOpt.get();

        return new ScheduleAccountPair(store.getSchedule(), account);
    }


}
