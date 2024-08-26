package com.ss6051.backendspring.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("ERR001", "Internal Server Error"),
    INVALID_INPUT_VALUE("ERR002", "잘못된 입력 값입니다."),

    // Account
    ACCOUNT_NOT_FOUND("ERR100", "사용자 정보를 찾을 수 없습니다."),
    ACCOUNT_DUPLICATION("ERR101", "Account is already exist"),
    ACCOUNT_LOGIN_FAILED("ERR102", "Account Login Failed"),
    ACCOUNT_KAKAO_LOGIN_FAILED("ERR103", "카카오 로그인 실패"),
    ACCOUNT_KAKAO_INFO_FAILED("ERR104", "카카오에서 정보 조회 실패"),

    // Store
    STORE_NOT_FOUND("ERR200", "매장 정보를 찾을 수 없습니다."),
    STORE_DUPLICATION("ERR201", "Store is already exist"),
    STORE_ALREADY_REGISTERED_MEMBER("ERR202", "회원이 이미 매장에 등록되어 있습니다."),

    // Role,
    ROLE_ACCESS_DENIED("ERR300", "해당 작업을 수행할 권한이 없습니다."),

    // Schedule
    SCHEDULE_NOT_FOUND("ERR400", "스케줄을 찾을 수 없습니다."),
    SCHEDULE_DUPLICATION("ERR401", "Schedule is already exist"),

    // BasicWorkSchedule
    BASIC_WORK_SCHEDULE_NOT_FOUND("ERR410", "기본 근무 스케줄을 찾을 수 없습니다."),
    BASIC_WORK_SCHEDULE_DUPLICATION("ERR411", "BasicWorkSchedule is already exist"),

    // ActualWorkSchedule
    ACTUAL_WORK_SCHEDULE_NOT_FOUND("ERR420", "실제 근무 스케줄을 찾을 수 없습니다."),
    ACTUAL_WORK_SCHEDULE_DUPLICATION("ERR421", "ActualWorkSchedule is already exist"),

    // One-Time Code
    CODE_NO_STORE_MATCHES("ERR500", "주어진 코드에 해당하는 매장 ID를 찾지 못했습니다."),

    // JWT
    JWT_TOKEN_EXPIRED("ERR600", "JWT Token Expired"),
    JWT_TOKEN_INVALID("ERR601", "JWT Token Invalid"),

    // Address
    ADDRESS_ALREADY_EXISTS("ERR700", "이미 다른 가게에 등록된 주소입니다."),;

    private final String code;
    private final String message;

}
