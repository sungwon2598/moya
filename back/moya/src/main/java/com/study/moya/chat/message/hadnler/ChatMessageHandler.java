package com.study.moya.chat.message.hadnler;

import com.study.moya.chat.message.ChatType;
import com.study.moya.chat.text.dto.chat.ChatDTO;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageHandler implements MessageHandler {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handle(ChatDTO chatDTO, String userEmail) {
        ChatDTO chatMessage = new ChatDTO(
                ChatType.CHAT,
                chatDTO.roomId(),
                userEmail,
                chatDTO.message(),
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), chatMessage);
    }

    @Override
    public boolean supports(ChatDTO chatDTO) {
        return ChatType.CHAT.equals(chatDTO.type());
    }
}