package com.study.moya.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.oauth.dto.OAuthTempMemberInfo;
import com.study.moya.oauth.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
//tes
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_KEY_PREFIX = "oauth:temp:";
    private static final String TOKEN_ACCESS_PREFIX = "token:access:";
    private static final String TOKEN_REFRESH_PREFIX = "token:refresh:";

    public OAuthTempMemberInfo getTempMemberInfo(String token) {
        String key = REDIS_KEY_PREFIX + token;
        log.info("Attempting to get data with key: {}", key);

        Object tempMemberObj = redisTemplate.opsForValue().get(key);
        log.info("Retrieved value from Redis: {}", tempMemberObj);

        if (tempMemberObj == null) {
            log.error("No data found for key: {}", key);
            throw new InvalidTokenException("임시 회원 정보를 찾을 수 없거나 만료되었습니다.");
        }

        try {
            String tempMemberJson;
            if (tempMemberObj instanceof String) {
                tempMemberJson = (String) tempMemberObj;
            } else {
                tempMemberJson = objectMapper.writeValueAsString(tempMemberObj);
            }

            log.info("JSON string to parse: {}", tempMemberJson);
            return objectMapper.readValue(tempMemberJson, OAuthTempMemberInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON for key {}: {}", key, e.getMessage());
            throw new RuntimeException("임시 회원 정보 변환 중 오류 발생", e);
        }
    }

    /**
     * 회원 가입 시 임시 토큰 생성 (약관 동의 및 별명, 자기소개 글 설정 이후 삭제)
     */
    public void saveTempMemberInfo(String token, OAuthTempMemberInfo info) {
        String key = REDIS_KEY_PREFIX + token;
        try {
            String value = objectMapper.writeValueAsString(info);
            log.info("Saving to Redis - Key: {}, Value: {}", key, value);
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, 2, TimeUnit.MINUTES);
            log.info("Successfully saved to Redis");
        } catch (JsonProcessingException e) {
            log.error("Failed to save to Redis", e);
            throw new RuntimeException("Failed to save temp member info", e);
        }
    }

    public void deleteTempMemberInfo(String token) {
        String key = REDIS_KEY_PREFIX + token;
        log.info("Deleting key from Redis: {}", key);
        Boolean result = redisTemplate.delete(key);
        log.info("Delete operation result: {}", result);
    }

    /**
     * Access Token과 Refresh Token 저장
     */
    public void saveTokens(String email, String accessToken, String refreshToken) {
        String accessKey = TOKEN_ACCESS_PREFIX + email;
        String refreshKey = TOKEN_REFRESH_PREFIX + email;

        try {
            redisTemplate.opsForValue().set(accessKey, accessToken);
            redisTemplate.expire(accessKey, 1, TimeUnit.HOURS);
            log.info("Access Token saved for email: {}", email);

            redisTemplate.opsForValue().set(refreshKey, refreshToken);
            redisTemplate.expire(refreshKey, 14, TimeUnit.DAYS);
            log.info("Refresh Token saved for email: {}", email);
        } catch (Exception e) {
            log.error("Failed to save tokens for email: {}", email, e);
            throw new RuntimeException("토큰 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * Access Token 조회
     */
    public String getAccessToken(String email) {
        String key = TOKEN_ACCESS_PREFIX + email;
        Object token = redisTemplate.opsForValue().get(key);
        if (token == null) {
            log.warn("Access Token not found for email: {}", email);
            return null;
        }
        return token.toString();
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String email) {
        String key = TOKEN_REFRESH_PREFIX + email;
        Object token = redisTemplate.opsForValue().get(key);
        if (token == null) {
            log.warn("Refresh Token not found for email: {}", email);
            return null;
        }
        return token.toString();
    }

    /**
     * 모든 토큰 삭제 (로그아웃 시 사용)
     */
    public void deleteAllTokens(String email) {
        String accessKey = TOKEN_ACCESS_PREFIX + email;
        String refreshKey = TOKEN_REFRESH_PREFIX + email;

        try {
            Boolean accessDeleted = redisTemplate.delete(accessKey);
            Boolean refreshDeleted = redisTemplate.delete(refreshKey);

            log.info("Tokens deleted for email: {} (Access: {}, Refresh: {})",
                    email, accessDeleted, refreshDeleted);
        } catch (Exception e) {
            log.error("Failed to delete tokens for email: {}", email, e);
            throw new RuntimeException("토큰 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * Access Token만 삭제
     */
    public void deleteAccessToken(String email) {
        String key = TOKEN_ACCESS_PREFIX + email;
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.info("Access Token deleted for email: {} (result: {})", email, deleted);
        } catch (Exception e) {
            log.error("Failed to delete Access Token for email: {}", email, e);
            throw new RuntimeException("Access Token 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * Refresh Token만 삭제
     */
    public void deleteRefreshToken(String email) {
        String key = TOKEN_REFRESH_PREFIX + email;
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.info("Refresh Token deleted for email: {} (result: {})", email, deleted);
        } catch (Exception e) {
            log.error("Failed to delete Refresh Token for email: {}", email, e);
            throw new RuntimeException("Refresh Token 삭제 중 오류가 발생했습니다.", e);
        }
    }

    // 디버그용 메소드
    public void debugRedisKeys() {
        try {
            Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
            log.info("All OAuth temp keys in Redis: {}", keys);

            for (String key : keys) {
                Object value = redisTemplate.opsForValue().get(key);
                log.info("Key: {}, Value: {}", key, value);
            }
        } catch (Exception e) {
            log.error("Failed to debug Redis keys", e);
        }
    }
}
