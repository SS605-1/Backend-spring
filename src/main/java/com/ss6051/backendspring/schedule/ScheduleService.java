package com.ss6051.backendspring.schedule;

import com.ss6051.backendspring.auth.AuthService;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.schedule.domain.ActualWorkSchedule;
import com.ss6051.backendspring.schedule.domain.BasicWorkSchedule;
import com.ss6051.backendspring.schedule.domain.Schedule;
import com.ss6051.backendspring.schedule.dto.actual.ActualWorkCreationDTO;
import com.ss6051.backendspring.schedule.dto.basic.BasicWorkCreationDTO;
import com.ss6051.backendspring.schedule.dto.basic.BasicWorkUpdateDTO;
import com.ss6051.backendspring.schedule.repository.ActualWorkScheduleRepository;
import com.ss6051.backendspring.schedule.repository.BasicWorkScheduleRepository;
import com.ss6051.backendspring.schedule.repository.ScheduleRepository;
import com.ss6051.backendspring.store.StoreService;
import com.ss6051.backendspring.store.domain.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final BasicWorkScheduleRepository basicWorkScheduleRepository;
    private final ActualWorkScheduleRepository actualWorkScheduleRepository;

    private final StoreService storeService;
    private final AuthService authService;

    // 생각해야 할 부분이 많다.
    /*
    일단 기본 근무 일정 부터.
    1. 일정 생성 시 어떻게 관리흘 할까?
    2. 일정 생성 시 겹치는 일정이 있는지 확인하는 로직은 어떻게 구현할까?

    TODO 결론: ERD 설계를 다시 해야 할 것 같다.

     */

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
    public BasicWorkSchedule createBasicWorkSchedule(BasicWorkCreationDTO dto) {

        ScheduleAccountPair pair = getScheduleAndAccount(dto.storeId(), dto.accountId());

        // store와 account로 BasicWorkSchedule 생성
        BasicWorkSchedule schedule = BasicWorkSchedule.builder()
                .account(pair.account())
                .schedule(pair.schedule())
                .dayOfWeek(dto.basicWorkDTO().dayOfWeek())
                .startTime(dto.basicWorkDTO().startTime())
                .endTime(dto.basicWorkDTO().endTime())
                .build();

        return basicWorkScheduleRepository.save(schedule);
    }

    // 스케줄 내 한 회원의 모든 기본 근무 일정 조회
    @Transactional(readOnly = true)
    public List<BasicWorkSchedule> findAllBasicWorkSchedule(long storeId, long accountId) {
        ScheduleAccountPair pair = getScheduleAndAccount(storeId, accountId);
        return basicWorkScheduleRepository.findAllByScheduleAndAccount(pair.schedule(), pair.account());
    }

    @Transactional
    public List<BasicWorkSchedule> updateBasicWorkSchedule(BasicWorkUpdateDTO dto) {
        // store 와 account 로 BasicWorkSchedule 조회
        List<BasicWorkSchedule> allBasicWorkSchedule = findAllBasicWorkSchedule(dto.storeId(), dto.accountId());

        // 조회한 모든 BasicWorkSchedule 삭제
        basicWorkScheduleRepository.deleteAll(allBasicWorkSchedule);

        // store 와 account 를 공통으로 하고, BasicWorkUpdateDTO 안에 있는 BasicWorkDTO 들로 BasicWorkSchedule 생성
        ScheduleAccountPair pair = getScheduleAndAccount(dto.storeId(), dto.accountId());
        List<BasicWorkSchedule> schedules = dto.basicWorkDTO().stream()
                .map(basicWorkDTO -> BasicWorkSchedule.builder()
                        .account(pair.account())
                        .schedule(pair.schedule())
                        .dayOfWeek(basicWorkDTO.dayOfWeek())
                        .startTime(basicWorkDTO.startTime())
                        .endTime(basicWorkDTO.endTime())
                        .build())
                .toList();

        return basicWorkScheduleRepository.saveAll(schedules);
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


    // id로 스케줄 조회해 삭제
    @Transactional
    public void deleteBasicWorkSchedule(Long id) {
        basicWorkScheduleRepository.deleteById(id);
    }

    @SuppressWarnings("unused")
    public void deleteSchedule() {
        // Store 가 삭제될 때 CASCADE 로 삭제될 것이므로 별도로 삭제할 필요 없음
    }

    private ScheduleAccountPair getScheduleAndAccount(long storeId, long accountId) {
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

    private record ScheduleAccountPair(Schedule schedule, Account account) {
    }
}
