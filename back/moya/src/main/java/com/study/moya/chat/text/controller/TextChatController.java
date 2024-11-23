package com.study.moya.chat.text.controller;

import com.study.moya.chat.text.dto.chat.ChatDTO;
import com.study.moya.chat.text.dto.chat.MessageType;
import com.study.moya.chat.text.dto.chatroom.ChatRoomDTO;
import com.study.moya.chat.text.service.ChatService;
import java.security.Principal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TextChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void handleChatMessage(@Payload ChatDTO chatDTO, Principal principal) {
        String userEmail = principal.getName();
        log.debug("채팅 메시지 처리 중 - 사용자: {}, 타입: {}", userEmail, chatDTO.type());

        switch (chatDTO.type()) {
            case JOIN -> handleJoin(chatDTO, userEmail);
            case CHAT -> handleChat(chatDTO, userEmail);
            case LEAVE -> handleLeave(chatDTO, userEmail);
            default -> {
                log.warn("지원되지 않는 메세지 타입 : {}", chatDTO.type());
                throw new IllegalStateException("잘못된 메세지 형식입니다. : " + chatDTO.type());
            }
        }
    }

    @PostMapping("/room/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, Principal principal) {
        String userEmail = principal.getName();
        log.debug("채팅방 나가기 요청 - 사용자: {}, 방 ID: {}", userEmail, roomId);

        try {
            chatService.removeUserFromRoom(roomId, userEmail);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("채팅방 나가기 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String roomId, Principal principal) {
        String userEmail = principal.getName();
        log.debug("채팅방 삭제 요청 - 사용자: {}, 방 ID: {}", userEmail, roomId);

        try {
            ChatRoomDTO room = chatService.findRoomById(roomId);
            if (!userEmail.equals(room.getCreator())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            chatService.deleteRoom(roomId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("채팅방 삭제 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    private void handleJoin(ChatDTO chatDTO, String userEmail) {
        chatService.addUserToRoom(chatDTO.roomId(), userEmail);

        ChatDTO joinMessage = new ChatDTO(
                MessageType.JOIN,
                chatDTO.roomId(),
                userEmail,
                userEmail + "님이 입장하였습니다.",
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), joinMessage);
    }

    private void handleChat(ChatDTO chatDTO, String userEmail) {
        ChatDTO chatMessage = new ChatDTO(
                MessageType.CHAT,
                chatDTO.roomId(),
                userEmail,
                chatDTO.message(),
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), chatMessage);
    }

    private void handleLeave(ChatDTO chatDTO, String userEmail) {
        ChatDTO leaveMessage = new ChatDTO(
                MessageType.LEAVE,
                chatDTO.roomId(),
                userEmail,
                userEmail + "님이 나갔습니다.",
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.roomId(), leaveMessage);
    }
}