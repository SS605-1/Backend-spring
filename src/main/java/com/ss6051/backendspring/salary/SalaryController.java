package com.ss6051.backendspring.salary;

import com.ss6051.backendspring.salary.dto.SalaryCalculateDTO;
import com.ss6051.backendspring.salary.dto.SalaryRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @Operation(summary = "특정 기간 동안의 실제 근무 시간 기반 급여 계산",
            description = "특정 기간 동안의 사용자의 실제 근무 시간을 기반으로 급여를 계산합니다. 주 단위로 계산하는 것을 권장합니다(정확도 높은 주휴수당 계산).",
            tags = {"salary"}
    )
    @GetMapping("/calculate-with-date")
    public ResponseEntity<Long> getSalary(@RequestBody SalaryRequestDTO dto) {

        Long response = salaryService.calculateSalary(dto.accountId(),dto.storeId(), dto.startDate(), dto.endDate());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 기간 동안의 실제 근무 시간 기반 급여 계산",
            description = "특정 기간 동안의 사용자의 실제 근무 시간을 기반으로 급여를 계산합니다. 주 단위로 계산하는 것을 권장합니다(정확도 높은 주휴수당 계산).",
            tags = {"salary"}
    )
    @GetMapping("/calculate")
    public ResponseEntity<List<SalaryCalculateDTO>> getSalary(@RequestParam("storeId") Long storeId) {

        List<SalaryCalculateDTO> salaryCalculateDTOS = salaryService.calculateSalary(storeId);

        return ResponseEntity.ok(salaryCalculateDTOS);
    }

}
