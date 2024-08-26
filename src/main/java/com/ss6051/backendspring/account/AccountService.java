package com.ss6051.backendspring.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ss6051.backendspring.account.domain.AuthTokens;
import com.ss6051.backendspring.account.dto.KakaoAccessTokenDto;
import com.ss6051.backendspring.account.dto.KakaoAccountTokenDto;
import com.ss6051.backendspring.account.dto.LoginResponseDto;
import com.ss6051.backendspring.account.tool.AuthTokensGenerator;
import com.ss6051.backendspring.global.domain.Account;
import com.ss6051.backendspring.global.exception.CustomException;
import com.ss6051.backendspring.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.ss6051.backendspring.Secret.*;

/**
 * 카카오 로그인을 위한 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthTokensGenerator authTokensGenerator;

    /**
     * 카카오 서버에 인가 코드로 토큰 요청을 위한 HttpEntity 생성
     *
     * @param code FE에서 받아온 인가 코드
     * @return {@code HttpEntity<MultiValueMap<String, String>>} Http 요청을 위한 HttpEntity
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
     *
     * @param kakaoAccessToken 카카오 액세스 토큰 값
     * @return {@code HttpEntity<MultiValueMap<String, String>>} Http 요청을 위한 HttpEntity
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
     *
     * @return ObjectMapper
     */
    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    /**
     * 카카오 사용자 정보 가져오기
     *
     * @param code 카카오 액세스 토큰 값
     * @return {@code LoginResponseDto} 사용자 정보
     */
    @Transactional
    public LoginResponseDto kakaoLogin(String code) {
        // 1. 인가 코드 -> 액세스 토큰 요청
        KakaoAccessTokenDto kakaoAccessToken = getKakaoAccessToken(code);

        // 2. 액세스 토큰 -> 사용자 정보 요청
        KakaoAccountTokenDto kakaoAccountTokenDto = getKakaoInfo(kakaoAccessToken.getAccess_token());

        // 3. 사용자 정보로 회원가입 및 로그인 처리
        Long kakaoId = kakaoAccountTokenDto.getId();
        AuthTokens authtokens = authTokensGenerator.generate(kakaoId.toString());

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .id(kakaoId)
                .nickname(kakaoAccountTokenDto.getKakaoAccount().getProfile().getNickname())
                .profile_image_url(kakaoAccountTokenDto.getKakaoAccount().getProfile().getProfile_image_url())
                .thumbnail_image_url(kakaoAccountTokenDto.getKakaoAccount().getProfile().getThumbnail_image_url())
                .token(authtokens)
                .build();

        // 기존 회원 여부 검사; 회원 가입 처리
        Optional<Account> existAccount = accountRepository.findById(kakaoId);
        try {
            // 신규 회원 가입 처리
            if (existAccount.isEmpty()) {
                Account newAccount = Account.builder()
                        .id(loginResponseDto.getId())
                        .profile_image_url(loginResponseDto.getProfile_image_url())
                        .thumbnail_image_url(loginResponseDto.getThumbnail_image_url())
                        .nickname(loginResponseDto.getNickname())
                        .build();
                accountRepository.save(newAccount);
                log.info("신규 회원 가입처리: id={}, nickname={}", newAccount.getId(), newAccount.getNickname());

            }
            log.info("회원 로그인: id={}, nickname={}", loginResponseDto.getId(), loginResponseDto.getNickname());
            return loginResponseDto;
        } catch (Exception e) {
            log.error("회원 가입/로그인 실패: id={}, nickname={}", loginResponseDto.getId(), loginResponseDto.getNickname(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Account 객체를 ID로 조회
     *
     * @param kakaoId 카카오 ID
     * @return {@code Optional<Account>} Account 객체
     */
    @Transactional(readOnly = true)
    public Account findAccount(Long kakaoId) {
        Optional<Account> byId = accountRepository.findById(kakaoId);
        if (byId.isEmpty()) {
            log.error("findAccount() error: entity not found by kakaoId={}", kakaoId);
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND, kakaoId.toString());
        }
        Account account = byId.get();
        // 강제로 lazy-loading을 트리거
        account.getStoreAccounts().size();
        return account;
    }

    /**
     * 카카오 서버에 액세스 토큰으로 사용자 정보 요청
     *
     * @param kakaoAccessToken 카카오 액세스 토큰 값
     * @return {@code KakaoAccountTokenDto} 사용자 정보
     */
    private KakaoAccountTokenDto getKakaoInfo(String kakaoAccessToken) {
        // 카카오 서버에 토큰으로 사용자 정보 요청
        HttpEntity<MultiValueMap<String, String>> kakaoAccountInfoRequest = createKakaoAccountInfoRequestToken(kakaoAccessToken);

        // 카카오 서버로부터 받은 사용자 정보를 저장
        ResponseEntity<String> accountInfoResponse = new RestTemplate().exchange(KAKAO_USER_INFO_URI, HttpMethod.POST, kakaoAccountInfoRequest, String.class);

        // 파싱
        ObjectMapper objectMapper = getObjectMapper();
        KakaoAccountTokenDto kakaoAccountTokenDto;
        try {
            kakaoAccountTokenDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountTokenDto.class);
        } catch (JsonProcessingException e) {
            log.error("getKakaoInfo() error: accessToken={}", kakaoAccessToken, e);
            throw new CustomException(ErrorCode.ACCOUNT_KAKAO_INFO_FAILED, e.getMessage());
        }
        return kakaoAccountTokenDto;
    }

    /**
     * 카카오 서버에 인가 코드로 토큰 요청
     *
     * @param code FE에서 받아온 인가 코드
     * @return KakaoAccessTokenDto
     */
    private KakaoAccessTokenDto getKakaoAccessToken(String code) {
        // 카카오 서버에 인가 코드로 토큰 요청
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = createKakaoAuthorizeRequestToken(code);


        KakaoAccessTokenDto kakaoAccessTokenDto;
        try {
            // 카카오 서버로부터 받은 토큰을 저장
            ResponseEntity<String> accessTokenResponse = new RestTemplate().exchange(KAKAO_TOKEN_URI, HttpMethod.POST, kakaoTokenRequest, String.class);

            // 받은 토큰을 KakaoTokenDto로 파싱
            ObjectMapper objectMapper = getObjectMapper();

            kakaoAccessTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoAccessTokenDto.class);
        } catch (HttpClientErrorException e) {
            log.error("getKakaoAccessToken() error: code={}, message={}", code, e.getMessage());
            throw new CustomException(ErrorCode.ACCOUNT_KAKAO_LOGIN_FAILED, e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            log.error("getKakaoAccessToken() error: code={}", code, e);
            throw new CustomException(ErrorCode.ACCOUNT_KAKAO_LOGIN_FAILED, e.getMessage());
        }

        return kakaoAccessTokenDto;
    }
}
