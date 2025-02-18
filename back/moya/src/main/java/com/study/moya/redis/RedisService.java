package com.study.moya.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
//tes
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "oauth:temp:";
    private static final String TOKEN_ACCESS_PREFIX = "token:access:";
    private static final String TOKEN_REFRESH_PREFIX = "token:refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";


    public void deleteTempMemberInfo(String token) {
        String key = REDIS_KEY_PREFIX + token;
        log.info("Deleting key from Redis: {}", key);
        Boolean result = redisTemplate.delete(key);
        log.info("Delete operation result: {}", result);
    }

    /**
     * Access Token과 Refresh Token 저장
     */
    public void saveTokens(String memberId, String refreshToken) {
//        String accessKey = TOKEN_ACCESS_PREFIX + uniqueIdentifier;
        String refreshKey = TOKEN_REFRESH_PREFIX + memberId;

        try {
//            redisTemplate.opsForValue().set(accessKey, accessToken);
//            redisTemplate.expire(accessKey, 1, TimeUnit.HOURS);
//            log.info("Access Token saved for email: {}", email)

            Map<String, String> tokenInfo = new HashMap<>();
            tokenInfo.put("refreshToken", refreshToken);

            redisTemplate.opsForHash().putAll(refreshKey, tokenInfo);
            redisTemplate.expire(refreshKey, 7, TimeUnit.DAYS);
            log.info("Token info saved for identifier: {}", memberId);

        } catch (Exception e) {
            log.error("Failed to save tokens and email for identifier: {}", memberId, e);
            throw new RuntimeException("토큰과 이메일 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * Access Token 조회
     */
    public String getAccessToken(String uniqueIdentifier) {
        String key = TOKEN_ACCESS_PREFIX + uniqueIdentifier;
        Object token = redisTemplate.opsForValue().get(key);
        if (token == null) {
            log.warn("Access Token not found for email: {}", uniqueIdentifier);
            return null;
        }
        return token.toString();
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String memberId) {
        String key = TOKEN_REFRESH_PREFIX + memberId;
        String refreshToken = (String) redisTemplate.opsForHash().get(key, "refreshToken");

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token not found");
        }
        return refreshToken;
    }

    /**
     * RefreshToken의 Email 조회
     */
    public String getEmailByIdentifier(String uniqueIdentifier) {
        String key = TOKEN_REFRESH_PREFIX + uniqueIdentifier;
        String email = (String) redisTemplate.opsForHash().get(key, "email");

        if (email == null) {
            throw new RuntimeException("Email not found");
        }
        return email;
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
    public void deleteRefreshToken(String memberId) {
        String key = TOKEN_REFRESH_PREFIX + memberId;
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.info("Refresh Token deleted for email: {} (result: {})", memberId, deleted);
        } catch (Exception e) {
            log.error("Failed to delete Refresh Token for email: {}", memberId, e);
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

    /**
     * 액세스 토큰을 블랙리스트에 추가
     * 토큰의 남은 유효시간만큼 블랙리스트에 보관
     */
    public void addToBlacklist(String accessToken, long expirationTime) {
        String key = BLACKLIST_PREFIX + accessToken;
        try {
            long ttl = expirationTime - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
                log.info("Access token added to blacklist with TTL: {} ms", ttl);
            }
        } catch (Exception e) {
            log.error("Failed to add token to blacklist", e);
            throw new RuntimeException("Failed to process blacklist", e);
        }
    }

    /**
     * 주어진 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check blacklist", e);
            return false;
        }
    }


    public boolean hasIdentifier(String identifier) {
        String key = TOKEN_REFRESH_PREFIX + identifier;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 로그인 시에 redis에 refreshToken이 존재하는지 확인
     */
    public String findIdentifierByMemberId(String memberId) {
        Set<String> keys = redisTemplate.keys(TOKEN_REFRESH_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                String storedEmail = (String) redisTemplate.opsForHash().get(key, "email");
                if (memberId.equals(storedEmail)) {
                    return key.substring(TOKEN_REFRESH_PREFIX.length());
                }
            }
        }
        return null;
    }
}
