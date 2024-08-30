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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.ss6051.backendspring.schedule.actual.domain.LaborLawConstant.NIGHT_SHIFT_END_HOUR;
import static com.ss6051.backendspring.schedule.actual.domain.LaborLawConstant.NIGHT_SHIFT_START_HOUR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActualWorkScheduleService {

    private final ActualWorkScheduleRepository actualWorkScheduleRepository;
    private final ScheduleService scheduleService;

    @Transactional
    public Long createActualWorkSchedule(ActualWorkCreationDTO dto, long accountId) {
        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), accountId);

        ActualWorkSchedule schedule = ActualWorkSchedule.builder()
                .account(pair.account())
                .schedule(pair.schedule())
                .startDateTime(dto.actualWorkDTO().startDateTime())
                .endDateTime(dto.actualWorkDTO().endDateTime())
                .build();
        ActualWorkSchedule save = actualWorkScheduleRepository.save(schedule);
        return save.getId();
    }

    @Transactional(readOnly = true)
    public List<ActualWorkSchedule> findAllActualWorkScheduleByStoreId(ActualWorkReadStoreAllDTO dto) {
        Schedule schedule = scheduleService.getSchedule(dto.storeId());
        return actualWorkScheduleRepository.findAllBySchedule(schedule);
    }

    @Transactional(readOnly = true)
    public List<ActualWorkSchedule> findAllActualWorkScheduleByStoreIdAndAccountId(ActualWorkReadStoreAccountAllDTO dto, long accountId) {
        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), accountId);
        return actualWorkScheduleRepository.findAllByScheduleAndAccount(pair.schedule(), pair.account());
    }

    @Transactional
    public ActualWorkSchedule updateActualWorkSchedule(ActualWorkUpdateDTO dto) {
        ActualWorkSchedule schedule = actualWorkScheduleRepository.findById(dto.id())
                .orElseThrow(() -> new CustomException(ErrorCode.ACTUAL_WORK_SCHEDULE_NOT_FOUND));

        if (dto.updateDto().startDateTime() == null) {
            schedule.updateEnd(dto.updateDto().endDateTime());
        } else {
            schedule.update(dto.updateDto().startDateTime(), dto.updateDto().endDateTime());
        }
        return actualWorkScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteActualWorkSchedule(ActualWorkDeleteDTO dto) {
        actualWorkScheduleRepository.deleteById(dto.id());
    }

    public List<ActualWorkSchedule> getTargetSchedule(ActualWorkTimeRequestDTO dto, long accountId) {
        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), accountId);
        return actualWorkScheduleRepository.findAllByScheduleAndAccountAndStartDateTimeBetween(pair.schedule(), pair.account(), dto.startDateTime(), dto.endDateTime());
    }

    /**
     * 사용자의 특정 기간 동안의 실제 근무 시간 합을 분 단위로 조회한다.
     * 근무 시간은 0분/30분 중 더 가까운 값으로 조정한다.
     * 시작일부터 종료일 사이에 시작한 것으로 기록된 실제 근무 시간이 조회 대상이다.
     *
     * @param dto
     * @param accountId
     * @return
     */
    @Transactional(readOnly = true)
    public WorkTimeResultDto getActualWorkTimeInPeriodOfUser(ActualWorkTimeRequestDTO dto, long accountId) {
        List<ActualWorkSchedule> allByScheduleAndAccount = getTargetSchedule(dto, accountId);

        long dayWorkMinute = 0;
        long nightWorkMinute = 0;
        int workDayCount = 0;

        for (ActualWorkSchedule schedule : allByScheduleAndAccount) {
            workDayCount += 1; // 기간 동안 근무한 날 수
            LocalDateTime startDateTime = adjustWorkTime(schedule.getStartDateTime());
            LocalDateTime endDateTime = adjustWorkTime(schedule.getEndDateTime());

            dayWorkMinute += Duration.between(startDateTime, endDateTime).toMinutes();

            // 야간 수당 계산 조건에 해당하는가?
            if (dto.hasMoreThanFiveEmployees()) {
                // 야간 근무 시간대; var1-: 시작일 0~6시, var2-: 시작일 22~24시, var3-: 종료일 0~6시, var4-: 종료일 22~24시
                final LocalDateTime var1s = startDateTime.toLocalDate().atStartOfDay();
                final LocalDateTime var1e = startDateTime.toLocalDate().atStartOfDay().plusHours(NIGHT_SHIFT_END_HOUR.getValue());

                final LocalDateTime var2s = startDateTime.toLocalDate().atStartOfDay().plusHours(NIGHT_SHIFT_START_HOUR.getValue());
                final LocalDateTime var2e = startDateTime.toLocalDate().atStartOfDay().plusDays(1);

                // 시작일과 종료일이 같은 날이면 중복 계산 방지
                if (startDateTime.toLocalDate().atStartOfDay().equals(endDateTime.toLocalDate().atStartOfDay())) {
                    final LocalDateTime[] starts = {var1s, var2s};
                    final LocalDateTime[] ends = {var1e, var2e};

                    for (int i = 0; i < 2; i++) {
                        Duration overlap = calculateOverlap(startDateTime, endDateTime, starts[i], ends[i]);
                        nightWorkMinute += overlap.toMinutes();
                    }
                } else {
                    final LocalDateTime var3s = endDateTime.toLocalDate().atStartOfDay();
                    final LocalDateTime var3e = endDateTime.toLocalDate().atStartOfDay().plusHours(NIGHT_SHIFT_END_HOUR.getValue());

                    final LocalDateTime var4s = endDateTime.toLocalDate().atStartOfDay().plusHours(NIGHT_SHIFT_START_HOUR.getValue());
                    final LocalDateTime var4e = endDateTime.toLocalDate().atStartOfDay().plusDays(1);

                    final LocalDateTime[] starts = {var1s, var2s, var3s, var4s};
                    final LocalDateTime[] ends = {var1e, var2e, var3e, var4e};

                    for (int i = 0; i < 4; i++) {
                        Duration overlap = calculateOverlap(startDateTime, endDateTime, starts[i], ends[i]);
                        nightWorkMinute += overlap.toMinutes();
                    }
                }
            }

            // 야간 근무 시간으로 계산된 부분은 주간 근무 시간에서 제외
            dayWorkMinute -= nightWorkMinute;

        }
        return new WorkTimeResultDto(dayWorkMinute, nightWorkMinute, workDayCount);
    }

    private LocalDateTime adjustWorkTime(LocalDateTime dateTime) {

        int minute = dateTime.getMinute();
        if (minute < 15) {
            dateTime = dateTime.withMinute(0);
        } else if (minute < 45) {
            dateTime = dateTime.withMinute(30);
        } else {
            dateTime = dateTime.withMinute(0).plusHours(1);
        }

        return dateTime;
    }


    private Duration calculateOverlap(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        LocalDateTime overlapStart = startDateTime.isAfter(rangeStart) ? startDateTime : rangeStart;
        LocalDateTime overlapEnd = endDateTime.isBefore(rangeEnd) ? endDateTime : rangeEnd;

        if (overlapStart.isBefore(overlapEnd)) {
            return Duration.between(overlapStart, overlapEnd);
        } else {
            return Duration.ZERO;
        }
    }
}
