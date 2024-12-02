package com.study.moya.chat.message.hadnler;

import com.study.moya.chat.text.dto.chat.ChatDTO;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageHandlerManager {
    private final List<MessageHandler> messageHandlers;

    public void handle(ChatDTO chatDTO, String userEmail) {
        MessageHandler handler = messageHandlers.stream()
                .filter(h -> h.supports(chatDTO))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("지원되지 않는 메세지 타입: {}", chatDTO.type());
                    return new UnsupportedMessageTypeException("지원되지 않는 메세지 타입입니다: " + chatDTO.type());
                });

        handler.handle(chatDTO, userEmail);
    }
}