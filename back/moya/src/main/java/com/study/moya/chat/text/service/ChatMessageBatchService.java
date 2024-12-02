package com.study.moya.chat.text.service;

import com.study.moya.chat.domain.ChatMessage;
import com.study.moya.chat.domain.RedisChatMessage;
import com.study.moya.chat.repository.ChatMessageRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageBatchService {
    private final RedisTemplate<String, RedisChatMessage> chatMessageRedisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private static final String CHAT_MESSAGES_KEY = "CHAT_MESSAGES:";  // 키 구조 통일
    private static final int BATCH_SIZE = 1000;

    @Scheduled(fixedDelay = 6000) // 1분마다 실행
    @Transactional
    public void processChatMessages() {
        log.info("Starting batch processing of chat messages");

        try {
            // 모든 채팅방의 메시지 키 패턴 조회
            Set<String> messageKeys = chatMessageRedisTemplate.keys(CHAT_MESSAGES_KEY + "*");

            if (messageKeys == null || messageKeys.isEmpty()) {
                log.debug("No messages to process");
                return;
            }

            for (String messageKey : messageKeys) {
                // 각 채팅방별로 처리되지 않은 메시지 조회
                List<RedisChatMessage> unprocessedMessages = chatMessageRedisTemplate.opsForList()
                        .range(messageKey, 0, BATCH_SIZE - 1);

                if (unprocessedMessages != null && !unprocessedMessages.isEmpty()) {
                    // JPA Entity로 변환 및 저장
                    List<ChatMessage> chatMessages = unprocessedMessages.stream()
                            .map(this::convertToEntity)
                            .toList();

                    chatMessageRepository.saveAll(chatMessages);

                    // 처리된 메시지 Redis에서 제거
                    chatMessageRedisTemplate.opsForList().trim(messageKey,
                            unprocessedMessages.size(), -1);

                    log.info("Processed and saved {} chat messages from key: {}",
                            unprocessedMessages.size(), messageKey);
                }
            }
        } catch (Exception e) {
            log.error("Error during batch processing of chat messages", e);
            throw e;  // 재시도를 위해 예외 던지기
        }
    }

    private ChatMessage convertToEntity(RedisChatMessage redisMessage) {
        ChatMessage entity = new ChatMessage();
        entity.setRoomId(redisMessage.getRoomId());
        entity.setSender(redisMessage.getSender());
        entity.setMessage(redisMessage.getMessage());
        entity.setTimestamp(redisMessage.getTimestamp());
        entity.setType(redisMessage.getType());
        entity.setSystemMessageType(redisMessage.getSystemMessageType());
        return entity;
    }
}