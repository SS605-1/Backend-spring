package com.ss6051.backendspring.account.dto;

import lombok.Data;


/**
 * KakaoTokenDto
 * 프론트에서 받아온 인가 코드로 카카오 서버에 요청하여 받은 토큰을 저장하는 DTO
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response-body">카카오 Docs</a>
 */
@Data
public class KakaoAccessTokenDto {
    private String token_type; // Bearer
    private String access_token; // 사용자 액세스 토큰
    private String id_token; // OpenID Connect 활성화 시 발급되는 사용자 ID 토큰
    private int expires_in; // 액세스 토큰 만료 시간(초)
    private String refresh_token; // 사용자 리프레시 토큰 값
    private int refresh_token_expires_in; // 리프레시 토큰 만료 시간(초)
    private String scope; // 인증된 사용자의 정보 조회 권한 범위, 공백으로 구분. OpenID 활성화 시 openid 포함
}