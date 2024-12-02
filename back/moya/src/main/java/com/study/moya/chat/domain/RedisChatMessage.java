package com.study.moya.chat.domain;

import com.study.moya.chat.message.ChatType;
import com.study.moya.chat.message.SystemMessageType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisChatMessage {
    private String id;  // Redis unique ID
    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;
    private ChatType type;
    private SystemMessageType systemMessageType;
    private boolean processed;  // 배치 처리 여부
}
