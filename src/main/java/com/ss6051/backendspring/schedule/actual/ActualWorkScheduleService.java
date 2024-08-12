package com.ss6051.backendspring.schedule.actual;

import com.ss6051.backendspring.auth.exception.EntityNotFoundByIdException;
import com.ss6051.backendspring.schedule.actual.domain.ActualWorkSchedule;
import com.ss6051.backendspring.schedule.actual.dto.*;
import com.ss6051.backendspring.schedule.common.ScheduleService;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.schedule.common.domain.ScheduleAccountPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActualWorkScheduleService {

    private final ActualWorkScheduleRepository actualWorkScheduleRepository;
    private final ScheduleService scheduleService;

    @Transactional
    public void createActualWorkSchedule(ActualWorkCreationDTO dto) {
        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), dto.accountId());

        ActualWorkSchedule schedule = ActualWorkSchedule.builder()
                .account(pair.account())
                .schedule(pair.schedule())
                .startDateTime(dto.actualWorkDTO().startDateTime())
                .endDateTime(dto.actualWorkDTO().endDateTime())
                .build();
        actualWorkScheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<ActualWorkSchedule> findAllActualWorkScheduleByStoreId(ActualWorkReadStoreAllDTO dto) {
        Schedule schedule = scheduleService.getSchedule(dto.storeId());
        return actualWorkScheduleRepository.findAllBySchedule(schedule);
    }

    @Transactional(readOnly = true)
    public List<ActualWorkSchedule> findAllActualWorkScheduleByStoreIdAndAccountId(ActualWorkReadStoreAccountAllDTO dto) {
        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), dto.accountId());
        return actualWorkScheduleRepository.findAllByScheduleAndAccount(pair.schedule(), pair.account());
    }

    @Transactional
    public ActualWorkSchedule updateActualWorkSchedule(ActualWorkUpdateDTO dto) {
        ActualWorkSchedule schedule = actualWorkScheduleRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundByIdException("ActualWorkSchedule", dto.id()));

        schedule.update(dto.updateDto().startDateTime(), dto.updateDto().endDateTime());
        return actualWorkScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteActualWorkSchedule(ActualWorkDeleteDTO dto) {
        actualWorkScheduleRepository.deleteById(dto.id());
    }

}
