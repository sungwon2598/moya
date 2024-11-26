package com.study.moya.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.Oauth.dto.OAuthTempMemberInfo;
import com.study.moya.Oauth.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String REDIS_KEY_PREFIX = "oauth:temp:";

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