package com.study.moya.chat.text.controller;

import com.study.moya.chat.message.SystemMessageType;
import com.study.moya.chat.message.hadnler.ChatMessageHandlerManager;
import com.study.moya.chat.text.dto.chat.ChatDTO;
import com.study.moya.chat.text.dto.chatroom.ChatRoomDTO;
import com.study.moya.chat.text.service.ChatService;
import java.security.Principal;
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
    private final ChatMessageHandlerManager messageHandlerManager;

    @MessageMapping("/chat/message")
    public void handleChatMessage(@Payload ChatDTO chatDTO, Principal principal) {
        String userEmail = principal.getName();
        log.debug("채팅 메시지 처리 중 - 사용자: {}, 타입: {}", userEmail, chatDTO.type());

        messageHandlerManager.handle(chatDTO, userEmail);
    }

    @PostMapping("/room/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, Principal principal) {
        String userEmail = principal.getName();
        log.debug("채팅방 나가기 요청 - 사용자: {}, 방 ID: {}", userEmail, roomId);

        try {
            ChatDTO leaveMessage = ChatDTO.createSystemMessage(roomId, userEmail, SystemMessageType.LEAVE);
            messageHandlerManager.handle(leaveMessage, userEmail);
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
}