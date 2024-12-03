package com.study.moya.chat.text.dto.chat;

import java.time.LocalDateTime;

public record FileChatDTO(
        MessageType type,
        String roomId,
        String sender,
        String message,
        String fileUrl,
        LocalDateTime timestamp
) {
}
