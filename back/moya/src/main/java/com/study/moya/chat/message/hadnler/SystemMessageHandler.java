package com.study.moya.chat.message.hadnler;

import com.study.moya.chat.message.ChatType;
import com.study.moya.chat.message.SystemMessageType;
import com.study.moya.chat.text.dto.chat.ChatDTO;
import com.study.moya.chat.text.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemMessageHandler implements MessageHandler {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @Override
    public void handle(ChatDTO chatDTO, String userEmail) {
        if (chatDTO.systemMessageType() == SystemMessageType.JOIN) {
            chatService.addUserToRoom(chatDTO.roomId(), userEmail);
        } else if (chatDTO.systemMessageType() == SystemMessageType.LEAVE) {
            chatService.removeUserFromRoom(chatDTO.roomId(), userEmail);
        }

        ChatDTO systemMessage = ChatDTO.createSystemMessage(
                chatDTO.roomId(),
                userEmail,
                chatDTO.systemMessageType()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), systemMessage);
    }

    @Override
    public boolean supports(ChatDTO chatDTO) {
        return ChatType.SYSTEM.equals(chatDTO.type());
    }
}