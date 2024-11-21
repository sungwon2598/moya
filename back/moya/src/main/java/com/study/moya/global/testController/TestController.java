package com.study.moya.global.testController;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class TestController {
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public TestController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/test")
    public String testRedis() {
        try {
            String key = "test:key";
            String value = "Hello Redis!" + LocalDateTime.now();

            redisTemplate.opsForValue().set(key, value);
            String retrievedValue = (String) redisTemplate.opsForValue().get(key);

            return String.format("Redis 테스트 성공!\n저장된 값: %s\n조회된 값: %s",
                    value, retrievedValue);
        } catch (Exception e) {
            return "Redis 연결 실패: " + e.getMessage();
        }
    }
}