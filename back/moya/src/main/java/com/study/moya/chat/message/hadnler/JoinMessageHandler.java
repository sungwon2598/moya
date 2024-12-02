package com.study.moya.chat.message.hadnler;

import com.study.moya.chat.message.ChatType;
import com.study.moya.chat.text.dto.chat.ChatDTO;
import com.study.moya.chat.text.service.ChatService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JoinMessageHandler implements MessageHandler {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @Override
    public void handle(ChatDTO chatDTO, String userEmail) {
        chatService.addUserToRoom(chatDTO.roomId(), userEmail);

        ChatDTO joinMessage = new ChatDTO(
                ChatType.JOIN,
                chatDTO.roomId(),
                userEmail,
                userEmail + "님이 입장하였습니다.",
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), joinMessage);
    }

    @Override
    public boolean supports(ChatDTO chatDTO) {
        return ChatType.JOIN.equals(chatDTO.type());
    }
}
