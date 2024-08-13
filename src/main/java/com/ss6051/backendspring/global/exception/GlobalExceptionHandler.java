package com.ss6051.backendspring.global.exception;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @Operation(summary = "각종 예외처리 결과를 반환합니다.",
            description = "처리된 오류에 대해선 400과 에러코드를, 서버 오류에 대해선 500을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.info("handleCustomException", ex);
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), ex.getMessage());
        if (errorCode == ErrorCode.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @Operation(summary = "필수 요청 매개변수 누락 시 처리합니다.",
            description = "필수 요청 매개변수가 누락된 경우 400 오류와 적절한 메시지를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        log.info("handleMissingParams", ex);
        ErrorResponse errorResponse = new ErrorResponse("ERR002", "필수 요청 매개변수가 누락되었습니다.", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TypeMismatchException.class, ClassNotFoundException.class, MethodArgumentTypeMismatchException.class})
    @Operation(summary = "타입 불일치 시 처리합니다.",
            description = "타입이 일치하지 않는 경우 400 오류와 적절한 메시지를 반환합니다.")
    public ResponseEntity<ErrorResponse> handleTypeMismatchParams(MissingServletRequestParameterException ex) {
        log.info("handleMissingParams", ex);
        ErrorResponse errorResponse = new ErrorResponse("ERR002", "필수 요청 매개변수가 누락되었거나, 타입이 일치하지 않습니다.", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}