package com.ss6051.backendspring.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.*;

/**
 * 일회성 코드 생성 서비스
 */
@Component
@Slf4j
public class OneTimeCodeGenerator {
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 5;
    private static final long EXPIRATION_TIME_MS = 5 * 60 * 1000; // 5 minutes

    private final Set<String> usedCodes = new HashSet<>();
    private final Map<String, Long> codeToStoreMap = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    /**
     * 일회성 코드를 생성한다.
     * @param storeId 매장 ID
     * @return 생성된 일회성 코드
     */
    public String generateUniqueCode(Long storeId) {
        String code;
        do {
            code = generateRandomCode();
        } while (usedCodes.contains(code));

        usedCodes.add(code);
        codeToStoreMap.put(code, storeId);
        scheduleExpiration(code);

        log.info("Generated one-time code for store {}: {}", storeId, code);

        return code;
    }

    /**
     * 코드에 해당하는 매장 ID를 반환한다.
     * 해당하는 코드가 없으면 null을 반환한다.
     * @param code 일회성 코드
     * @return 매장 ID
     */
    public Long getStoreIdWithCode(String code) {
        if (!usedCodes.contains(code)) {
            return null;
        }
        return codeToStoreMap.get(code);
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    private void scheduleExpiration(final String code) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                usedCodes.remove(code);
                codeToStoreMap.remove(code);
                log.info("One-time code expired: {}", code);
            }
        }, EXPIRATION_TIME_MS);
    }
}

