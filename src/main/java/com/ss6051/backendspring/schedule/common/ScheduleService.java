package com.ss6051.backendspring.schedule.common;

import com.ss6051.backendspring.account.AccountService;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.validator.ExistsInDatabase;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.schedule.common.domain.ScheduleAccountPair;
import com.ss6051.backendspring.store.StoreService;
import com.ss6051.backendspring.store.domain.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final StoreService storeService;
    private final AccountService accountService;

    @Transactional
    public Schedule createSchedule(Store store) {
        Schedule schedule = Schedule.builder()
                .store(store)
                .lastModifiedBy(store.getOwner())
                .lastModifiedTime(LocalDateTime.now())
                .build();
        return scheduleRepository.save(schedule);
    }


    @SuppressWarnings("unused")
    public void deleteSchedule() {
        // Store 가 삭제될 때 CASCADE 로 삭제될 것이므로 별도로 삭제할 필요 없음
    }

    public ScheduleAccountPair getScheduleAndAccount(@ExistsInDatabase(type = Store.class) long storeId,
                                                     @ExistsInDatabase(type = Account.class) long accountId) {
        return new ScheduleAccountPair(getSchedule(storeId), accountService.findAccount(accountId));
    }

    public Schedule getSchedule(@ExistsInDatabase(type = Store.class) long storeId) {
        return storeService.findStore(storeId).getSchedule();

    }


}
