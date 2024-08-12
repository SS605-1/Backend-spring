package com.ss6051.backendspring.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ss6051.backendspring.account.dto.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/oauth2/kakao")
@Slf4j
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    /**
     * 카카오 로그인
     * @param code 인가 코드
     * @return {@code ResponseEntity<LoginResponseDto>} 카카오 로그인 성공 시 사용자 정보를 담은 ResponseEntity
     */
    @Operation(summary = "카카오 로그인",
            description = "FE에서 받아온 인가 코드를 통해 카카오 로그인을 수행합니다. 카카오 로그인 성공 시 사용자 정보를 담은 ResponseEntity를 반환합니다.",
            tags = {"auth"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "카카오 로그인 성공",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(value = """
                                            {
                                              "id": 1,
                                              "nickname": "john_doe",
                                              "profile_image_url": "https://example.com/profile.jpg",
                                              "thumbnail_image_url": "https://example.com/thumbnail.jpg",
                                              "token": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                                                "refreshToken": "d3c3N2NiMTk1OGNiMzNkNzRiOGNkY2QwMDk3.mklq2fo12mkldmfklmxlfmeklmlsmlfxmlkdmflxfsfnmlxkmdlfkmlk2x190u0t92uNGM3M.Dc0YzJmY2I1YjQxZWI5NDM1YzUzNTg0ZmY3NmUyN2I0NzU1YQ",
                                                "grantType": "Bearer",
                                                "expiresIn": 3600
                                              }
                                            }
                                            """)})),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                            content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(value = """
                                            {
                                              "message": "Internal Server Error"
                                            }""")}))
            })
    @GetMapping("")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam(name = "code") String code) {
        log.info("kakaoLogin() start");

        LoginResponseDto dto;
        try {
            dto = accountService.kakaoLogin(code);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("kakaoLogin() unknown error occurred. ",e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        log.info("kakaoLogin() end");
        return ResponseEntity.ok(dto);
    }

}
