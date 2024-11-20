package com.study.moya.chat.text.dto.chat;

import java.time.LocalDateTime;

public record ChatDTO(
        MessageType type,
        String roomId,
        String sender,
        String message,
        LocalDateTime timestamp
) {
}
