package com.ss6051.backendspring.salary;

import com.ss6051.backendspring.schedule.actual.ActualWorkScheduleService;
import com.ss6051.backendspring.schedule.actual.dto.ActualWorkTimeRequestDTO;
import com.ss6051.backendspring.schedule.actual.dto.WorkTimeResultDto;
import com.ss6051.backendspring.schedule.basic.BasicWorkScheduleService;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkReadDTO;
import com.ss6051.backendspring.store.StoreService;
import com.ss6051.backendspring.store.domain.Store;
import com.ss6051.backendspring.store.domain.StoreAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryService {

    private final StoreService storeService;
    private final ActualWorkScheduleService actualWorkScheduleService;
    private final BasicWorkScheduleService basicWorkScheduleService;

    @Transactional(readOnly = true)
    public Long calculateSalary(Long accountId, long storeId, LocalDate startDate, LocalDate endDate) {
        Store store = storeService.findStore(storeId);
        boolean hasMoreThanFiveEmployees = store.getEmployeeCount() >= 5; // 야간 수당 계산을 위한 조건

        StoreAccount storeAccount = storeService.getAccount(accountId, storeId);
        Long baseSalary = storeAccount.getBaseSalary(); // 기본급

        List<LocalDate[]> weeks = splitIntoWeeks(startDate, endDate);
        long totalDayWorkMins = 0, totalNightWorkMins = 0;
        long salary = 0;

        // 주휴수당 계산
        // 월요일~일요일까지 근무 일정이 있는 날 중에서 근무 시간의 합을 근무 일정이 있는 날의 수로 나눈 값을 가져오기
        int weeklyHolidayAllowance = 0; // 주휴수당 지급 대상 주
        AtomicReference<AtomicLong> holidayAllowanceDividend = new AtomicReference<>(new AtomicLong()); // 주휴수당 기준 주간 소정근로시간
        AtomicInteger holidayAllowanceDivisor = new AtomicInteger(); // 주휴수당 지급 대상 일수
        basicWorkScheduleService.findAllBasicWorkSchedule(new BasicWorkReadDTO(storeId, accountId)).stream()
                .filter(schedule -> schedule.getStartTime() != null && schedule.getEndTime() != null)
                .forEach(schedule -> {
                    holidayAllowanceDividend.updateAndGet(v -> new AtomicLong(v.addAndGet(Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes())));
                    holidayAllowanceDivisor.getAndIncrement();
                });

        for (LocalDate[] week : weeks) {
            WorkTimeResultDto result = actualWorkScheduleService.getActualWorkTimeInPeriodOfUser(
                    new ActualWorkTimeRequestDTO(storeId, accountId, hasMoreThanFiveEmployees, week[0].atStartOfDay(), week[1].atStartOfDay()));
            totalDayWorkMins += result.dayShiftMinute();
            totalNightWorkMins += result.nightShiftMinute();

            if (result.dayShiftMinute() + result.nightShiftMinute() >= 900) { // 주 15시간 이상 근무시 주휴수당 계산
                weeklyHolidayAllowance++;
            }
        }

        // 분 단위를 시간으로 바꾸고, 주간 근무시간에 기본급을 곱하고 야간 근무시간에는 1.5배를 곱하여 합산
        salary += (long) ((totalDayWorkMins / 60 * baseSalary) + (totalNightWorkMins / 60 * baseSalary * 1.5));

        // 주휴수당 계산: 주휴수당 지급 대상 주가 1주 이상일 때 (, 주휴수당 기준 소정근로일수가 0이 아닐 때-버그 방지)
        if (holidayAllowanceDivisor.get() > 0 && weeklyHolidayAllowance > 0) {
            // 기본급 * (주휴수당 기준 근로시간 / 주휴수당 지급 대상 일수)
            salary += baseSalary * holidayAllowanceDividend.get().get() / holidayAllowanceDivisor.get();
        }

        return salary;
    }

    // 시작 일 기준으로 7일씩 나누어진 주의 시작일과 종료일을 반환
    private List<LocalDate[]> splitIntoWeeks(LocalDate startDate, LocalDate endDate) {
        List<LocalDate[]> weeks = new ArrayList<>();

        LocalDate currentStart = startDate;
        while (currentStart.isBefore(endDate) || currentStart.equals(endDate)) {
            LocalDate currentEnd = currentStart.plusDays(6);
            if (currentEnd.isAfter(endDate)) {
                currentEnd = endDate;
            }
            weeks.add(new LocalDate[]{currentStart, currentEnd});
            currentStart = currentEnd.plusDays(1);
        }

        return weeks;
    }


}
