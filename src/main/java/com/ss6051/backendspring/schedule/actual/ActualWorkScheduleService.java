package com.ss6051.backendspring.schedule.actual;

import com.ss6051.backendspring.global.exception.CustomException;
import com.ss6051.backendspring.global.exception.ErrorCode;
import com.ss6051.backendspring.schedule.actual.domain.ActualWorkSchedule;
import com.ss6051.backendspring.schedule.actual.dto.*;
import com.ss6051.backendspring.schedule.common.ScheduleService;
import com.ss6051.backendspring.schedule.common.domain.Schedule;
import com.ss6051.backendspring.schedule.common.domain.ScheduleAccountPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                .orElseThrow(() -> new CustomException(ErrorCode.ACTUAL_WORK_SCHEDULE_NOT_FOUND));

        schedule.update(dto.updateDto().startDateTime(), dto.updateDto().endDateTime());
        return actualWorkScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteActualWorkSchedule(ActualWorkDeleteDTO dto) {
        actualWorkScheduleRepository.deleteById(dto.id());
    }

    /**
     * 사용자의 특정 기간 동안의 실제 근무 시간 합을 분 단위로 조회한다.
     * 시작일부터 종료일 사이에 기록된 실제 근무 시간이 조회 대상이며, 기준 시각은 dto 입력으로 받는다..
     * @param dto
     * @return
     */
    @Transactional(readOnly = true)
    public Long getActualWorkTimeInPeriodOfUser(ActualWorkTimeRequestDTO dto) {
        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), dto.accountId());
        List<ActualWorkSchedule> allByScheduleAndAccount = actualWorkScheduleRepository.findAllByScheduleAndAccount(pair.schedule(), pair.account());

        Long result = 0L;
        for (ActualWorkSchedule schedule : allByScheduleAndAccount) {
            LocalDateTime start = schedule.getStartDateTime();
            LocalDateTime end = schedule.getEndDateTime();
            LocalDateTime startCriteria = dto.startDate().atStartOfDay().plusHours(dto.criteriaHour());
            LocalDateTime endCriteria = dto.endDate().atStartOfDay().plusHours(dto.criteriaHour());
            if (!start.isBefore(startCriteria) && !end.isAfter(endCriteria)) {
                result += schedule.getActualWorkTimeMinute();
            }
        }
        return result;
    }
}
