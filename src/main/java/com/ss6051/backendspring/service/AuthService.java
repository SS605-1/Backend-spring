package com.ss6051.backendspring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ss6051.backendspring.dto.KakaoAccessTokenDto;
import com.ss6051.backendspring.dto.KakaoAccountTokenDto;
import com.ss6051.backendspring.dto.LoginResponseDto;
import com.ss6051.backendspring.domain.Account;
import com.ss6051.backendspring.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.ss6051.backendspring.Secret.*;

/**
 * 카카오 로그인을 위한 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;

    /**
     * 카카오 서버에 인가 코드로 토큰 요청
     * @param code FE에서 받아온 인가 코드
     * @return KakaoAccessTokenDto
     */
    @Transactional
    public KakaoAccessTokenDto getKakaoAccessToken(String code) {
        // 카카오 서버에 인가 코드로 토큰 요청
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = createKakaoAuthorizeRequestToken(code);

        // 카카오 서버로부터 받은 토큰을 저장
        ResponseEntity<String> accessTokenResponse = new RestTemplate().exchange(KAKAO_TOKEN_URI, HttpMethod.POST, kakaoTokenRequest, String.class);

        // 받은 토큰을 KakaoTokenDto로 파싱
        ObjectMapper objectMapper = getObjectMapper();

        KakaoAccessTokenDto kakaoAccessTokenDto = null;
        try {
            kakaoAccessTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoAccessTokenDto.class);
        } catch (JsonProcessingException e) {
            log.error("getKakaoAccessToken() error: code={}", code, e);
        }

        return kakaoAccessTokenDto;
    }

    /**
     * 카카오 사용자 정보 가져오기
     * @param kakaoAccessToken 카카오 액세스 토큰 값
     * @return ResponseEntity<LoginResponseDto> 로그인 응답 DTO를 담은 ResponseEntity
     */
    @Transactional
    public ResponseEntity<LoginResponseDto> kakaoLogin(String kakaoAccessToken) {
        Account account = getKakaoInfo(kakaoAccessToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setLoginSuccessful(true);
        loginResponseDto.setAccount(account);

        Account existOwner = accountRepository.findById(account.getId()).orElse(null);
        try {
            if (existOwner == null) {
                log.info("신규 회원 가입처리: id={}, nickname={}", account.getId(), account.getNickname());
                accountRepository.save(account);
            }
            loginResponseDto.setLoginSuccessful(true);

            log.info("회원 로그인: id={}, nickname={}", account.getId(), account.getNickname());
            return ResponseEntity.ok().body(loginResponseDto);

        } catch (Exception e) {
            loginResponseDto.setLoginSuccessful(false);
            return ResponseEntity.badRequest().body(loginResponseDto);
        }
    }

    /**
     * 카카오 서버에 액세스 토큰으로 사용자 정보 요청
     * @param kakaoAccessToken 카카오 액세스 토큰 값
     * @return Account 생성했거나 DB에 저장된 Account 객체
     */
    private Account getKakaoInfo(String kakaoAccessToken) {
        // 카카오 서버에 토큰으로 사용자 정보 요청
        HttpEntity<MultiValueMap<String, String>> kakaoAccountInfoRequest = createKakaoAccountInfoRequestToken(kakaoAccessToken);

        // 카카오 서버로부터 받은 사용자 정보를 저장
        ResponseEntity<String> accountInfoResponse = new RestTemplate().exchange(KAKAO_USER_INFO_URI, HttpMethod.POST, kakaoAccountInfoRequest, String.class);

        ObjectMapper objectMapper = getObjectMapper();
        KakaoAccountTokenDto kakaoAccountTokenDto = null;
        try {
            kakaoAccountTokenDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountTokenDto.class);
        } catch (JsonProcessingException e) {
            log.error("getKakaoInfo() error: accessToken={}", kakaoAccessToken, e);
        }

        assert kakaoAccountTokenDto != null;
        Long kakaoId = kakaoAccountTokenDto.getId();
        Optional<Account> existAccount = accountRepository.findById(kakaoId);
        if (existAccount.isEmpty()) {
            // 새로 가입하는 회원인 경우, Account 객체를 생성하여 반환
            KakaoAccountTokenDto.KakaoAccount.Profile profile = kakaoAccountTokenDto.getKakaoAccount().getProfile();
            Account newAccount = Account.builder()
                    .id(kakaoId)
                    .profile_image_url(profile.getProfile_image_url())
                    .thumbnail_image_url(profile.getThumbnail_image_url())
                    .nickname(profile.getNickname())
                    .build();
            log.info("신규 회원 생성: newAccount={}", newAccount);
            return newAccount;
        } else {
            return existAccount.get();
        }

    }

    /**
     * 카카오 서버에 인가 코드로 토큰 요청을 위한 HttpEntity 생성
     * @param code FE에서 받아온 인가 코드
     * @return HttpEntity<MultiValueMap<String, String>> Http 요청을 위한 HttpEntity
     */
    private static HttpEntity<MultiValueMap<String, String>> createKakaoAuthorizeRequestToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-request-body
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);
        params.add("client_secret", KAKAO_CLIENT_SECRET); // Optional

        return new HttpEntity<>(params, headers);
    }

    /**
     * 카카오 서버에 액세스 토큰으로 사용자 정보 요청을 위한 HttpEntity 생성
     * @param kakaoAccessToken 카카오 액세스 토큰 값
     * @return HttpEntity<MultiValueMap<String, String>> Http 요청을 위한 HttpEntity
     */
    private static HttpEntity<MultiValueMap<String, String>> createKakaoAccountInfoRequestToken(String kakaoAccessToken) {
        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info-request-header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return new HttpEntity<>(headers);
    }

    /**
     * 공용 ObjectMapper 생성
     * @return ObjectMapper
     */
    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
