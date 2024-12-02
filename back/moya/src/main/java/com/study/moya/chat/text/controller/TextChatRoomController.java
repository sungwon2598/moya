package com.study.moya.chat.text.controller;

import com.study.moya.chat.text.dto.chatroom.ChatRoomDTO;
import com.study.moya.chat.text.service.ChatService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ws/chat")
public class TextChatRoomController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getUserRooms(Authentication authentication) {
        log.info("get user rooms");
        List<ChatRoomDTO> rooms = chatService.findAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/create")
    public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody String roomName, Principal principal) {
        String userEmail = principal.getName();
        log.debug("채팅방 생성 요청 - 사용자: {}, 방 이름: {}", userEmail, roomName);

        ChatRoomDTO chatRoom = chatService.createTextRoom(roomName, userEmail);
        chatService.addUserToRoom(chatRoom.getRoomId(), userEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomDTO> getRoom(@PathVariable String roomId) {
        try {
            ChatRoomDTO chatRoom = chatService.findRoomById(roomId);
            return ResponseEntity.ok(chatRoom);
        } catch (IllegalArgumentException e) {
            log.warn("Finding room by ID: {}", roomId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/room/{roomId}/leave")
    public ResponseEntity<?> leaveRoom(@PathVariable String roomId, Principal principal) {
        String userEmail = principal.getName();
        log.debug("채팅방 나가기 요청 - 사용자: {}, 방 ID: {}", userEmail, roomId);

        try {
            chatService.removeUserFromRoom(roomId, userEmail);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("채팅방 나가기 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId, Principal principal) {
        String userEmail = principal.getName();
        log.debug("채팅방 삭제 요청 - 사용자: {}, 방 ID: {}", userEmail, roomId);

        try {
            ChatRoomDTO room = chatService.findRoomById(roomId);
            if (!room.getCreator().equals(userEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            chatService.deleteRoom(roomId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("채팅방 삭제 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}