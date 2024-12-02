package com.study.moya.chat.text.dto.chat;

import com.study.moya.chat.message.ChatType;
import com.study.moya.chat.message.SystemMessageType;
import java.time.LocalDateTime;

public record ChatDTO(
        ChatType type,
        String roomId,
        String sender,
        String message,
        LocalDateTime timestamp,
        SystemMessageType systemMessageType
) {
    public ChatDTO(ChatType type, String roomId, String sender, String message, LocalDateTime timestamp) {
        this(type, roomId, sender, message, timestamp, null);
    }

    public static ChatDTO createSystemMessage(String roomId, String sender, SystemMessageType messageType) {
        return new ChatDTO(
                ChatType.SYSTEM,
                roomId,
                sender,
                messageType.generateMessage(sender),
                LocalDateTime.now(),
                messageType
        );
    }
}