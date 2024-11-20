package com.study.moya.chat.text.controller;

import com.study.moya.chat.text.dto.chat.ChatDTO;
import com.study.moya.chat.text.dto.chat.MessageType;
import com.study.moya.chat.text.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TextChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void handleChatMessage(@Payload ChatDTO chatDTO) {
        switch (chatDTO.type()) {
            case JOIN -> handleJoin(chatDTO);
            case CHAT -> handleChat(chatDTO);
            case LEAVE -> handleLeave(chatDTO);
            default -> {
                log.warn("지원되지 않는 메세지 타입 : {}", chatDTO.type());
                throw new IllegalStateException(" 잘못된 메세지 형식입니다. : " + chatDTO.type());
            }
        }
    }

    private void handleJoin(ChatDTO chatDTO) {
        chatService.addUserToRoom(chatDTO.roomId(), chatDTO.sender());

        ChatDTO joinMessage = new ChatDTO(
                MessageType.JOIN,
                chatDTO.roomId(),
                chatDTO.sender(),
                chatDTO.sender() + "님이 입장하였습니다.",
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), joinMessage);
    }

    private void handleChat(ChatDTO chatDTO) {
        ChatDTO chatMessage = new ChatDTO(
                MessageType.CHAT,
                chatDTO.roomId(),
                chatDTO.sender(),
                chatDTO.message(),
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), chatMessage);
    }

    private void handleLeave(ChatDTO chatDTO) {
        ChatDTO leaveMessage = new ChatDTO(
                MessageType.LEAVE,
                chatDTO.roomId(),
                chatDTO.sender(),
                chatDTO.sender() + "님이 나갔습니다.",
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), leaveMessage);
    }

}
