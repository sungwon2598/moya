package com.study.moya.chat.text.dto.chat;

import com.study.moya.chat.message.ChatType;
import java.time.LocalDateTime;

public record ChatDTO(
        ChatType type,
        String roomId,
        String sender,
        String message,
        LocalDateTime timestamp
) {
}