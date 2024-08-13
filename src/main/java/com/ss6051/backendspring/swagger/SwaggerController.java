package com.ss6051.backendspring.swagger;

import com.ss6051.backendspring.global.exception.CustomException;
import com.ss6051.backendspring.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    // Dev only: Redirect to Swagger UI
    @GetMapping(path = {"/", "", "/swagger"})
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }

    @ExceptionHandler(CustomException.class)
    @Operation(tags = "error", summary = "각종 예외처리 결과를 반환합니다.",
            description = "처리된 오류에 대해선 400과 에러코드를, 서버 오류에 대해선 500을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/예외 핸들링은 여기에서 확인하세요.")
    public ResponseEntity<ErrorResponse> handleCustomException() {
        return ResponseEntity.badRequest().body(new ErrorResponse("ERR001", "예외가 발생했습니다.", null));
    }

}
