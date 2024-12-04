package com.study.moya.chat.text.dto.chat;

import com.study.moya.chat.domain.ChatMessage;
import com.study.moya.chat.message.ChatType;
import com.study.moya.chat.message.SystemMessageType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ChatMessageResponse {
    private Long id;
    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;
    private ChatType type;
    private SystemMessageType systemMessageType;

    public ChatMessageResponse(ChatMessage message) {
        this.id = message.getId();
        this.roomId = message.getRoomId();
        this.sender = message.getSender();
        this.message = message.getMessage();
        this.timestamp = message.getTimestamp();
        this.type = message.getType();
        this.systemMessageType = message.getSystemMessageType();
    }
}