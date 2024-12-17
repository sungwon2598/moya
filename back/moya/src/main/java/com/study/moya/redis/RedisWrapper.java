package com.study.moya.redis;

import com.study.moya.member.util.AESConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisWrapper {
    private final RedisService redisService;
    private final AESConverter aesConverter;

    public void saveTokens(String email, String refreshToken) {
        String encryptedEmail = aesConverter.convertToDatabaseColumn(email);
        redisService.saveTokens(encryptedEmail, refreshToken);
    }

    public String getRefreshToken(String email) {
        String encryptedEmail = aesConverter.convertToDatabaseColumn(email);
        return redisService.getRefreshToken(encryptedEmail);
    }

    public void deleteAllTokens(String email) {
        String encryptedEmail = aesConverter.convertToDatabaseColumn(email);
        redisService.deleteRefreshToken(encryptedEmail);
    }

    // 다른 필요한 메서드들도 동일한 패턴으로 구현
}
