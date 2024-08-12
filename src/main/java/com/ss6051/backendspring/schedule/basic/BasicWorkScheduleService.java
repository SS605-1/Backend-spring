package com.ss6051.backendspring.schedule.basic;

import com.ss6051.backendspring.schedule.basic.domain.BasicWorkSchedule;
import com.ss6051.backendspring.schedule.common.domain.ScheduleAccountPair;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkCreationDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkDeleteDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkReadDTO;
import com.ss6051.backendspring.schedule.basic.dto.BasicWorkUpdateDTO;
import com.ss6051.backendspring.schedule.common.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicWorkScheduleService {

    private final BasicWorkScheduleRepository basicWorkScheduleRepository;
    private final ScheduleService scheduleService;

    @Transactional
    public List<BasicWorkSchedule> createBasicWorkSchedule(BasicWorkCreationDTO dto) {

        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), dto.accountId());

        // store와 account로 BasicWorkSchedule 생성
        List<BasicWorkSchedule> schedules = new ArrayList<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            BasicWorkSchedule schedule = BasicWorkSchedule.builder()
                    .schedule(pair.schedule())
                    .account(pair.account())
                    .dayOfWeek(dayOfWeek)
                    .startTime(null)
                    .endTime(null)
                    .build();
            schedules.add(schedule);
        }

        return basicWorkScheduleRepository.saveAll(schedules);
    }

    // 스케줄 내 한 회원의 모든 기본 근무 일정 조회
    @Transactional(readOnly = true)
    public List<BasicWorkSchedule> findAllBasicWorkSchedule(BasicWorkReadDTO dto) {
        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), dto.accountId());
        return basicWorkScheduleRepository.findAllByScheduleAndAccount(pair.schedule(), pair.account());
    }

    @Transactional
    public BasicWorkSchedule updateBasicWorkSchedule(BasicWorkUpdateDTO dto) {

        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), dto.accountId());

        BasicWorkSchedule basicWorkSchedule = basicWorkScheduleRepository.findByScheduleAndAccountAndDayOfWeek(pair.schedule(), pair.account(), dto.basicWorkDTO().dayOfWeek())
                .orElseThrow(() -> new IllegalArgumentException("주어진 정보에 해당하는 기본 근무 시간을 찾을 수 없음"));

        basicWorkSchedule.update(dto.basicWorkDTO().startTime(), dto.basicWorkDTO().endTime());

        return basicWorkScheduleRepository.save(basicWorkSchedule);
    }

    @Transactional
    public void deleteBasicWorkSchedule(BasicWorkDeleteDTO dto) {
        ScheduleAccountPair pair = scheduleService.getScheduleAndAccount(dto.storeId(), dto.accountId());
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            Optional<BasicWorkSchedule> scheduleOpt = basicWorkScheduleRepository.findByScheduleAndAccountAndDayOfWeek(pair.schedule(), pair.account(), dayOfWeek);
            scheduleOpt.ifPresent(basicWorkSchedule -> basicWorkScheduleRepository.deleteById(basicWorkSchedule.getId()));
        }
    }
}
