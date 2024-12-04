package com.study.moya.chat.text.controller;

import com.study.moya.chat.domain.ChatRoom;
import com.study.moya.chat.text.dto.chat.ChatMessageResponse;
import com.study.moya.chat.text.dto.chatroom.ChatRoomDTO;
import com.study.moya.chat.text.dto.chatroom.ChatRoomResponse;
import com.study.moya.chat.text.service.ChatService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ws/chat")
public class TextChatRoomController {

    private final ChatService chatService;

    @GetMapping()
    public ResponseEntity<List<ChatRoomResponse>> getUserRooms(Principal principal) {
        List<ChatRoom> rooms = chatService.getUserChatRooms(principal.getName());
        List<ChatRoomResponse> responses = rooms.stream()
                .map(ChatRoomResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(
            @PathVariable String roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before,
            Principal principal) {
        try {
            List<ChatMessageResponse> messages = chatService.loadChatHistory(roomId, principal.getName(), before);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody String roomName, Principal principal) {
        ChatRoom chatRoom = chatService.createTextRoom(roomName, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ChatRoomResponse(chatRoom));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(@PathVariable String roomId, Principal principal) {
        try {
            ChatRoom chatRoom = chatService.findRoomById(roomId);
            if (!chatService.isRoomMember(roomId, principal.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(new ChatRoomResponse(chatRoom));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/room/{roomId}/join")
    public ResponseEntity<Void> joinRoom(@PathVariable String roomId, Principal principal) {
        try {
            chatService.addUserToRoom(roomId, principal.getName());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

//    @PostMapping("/room/{roomId}/leave")
//    public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, Principal principal) {
//        try {
//            chatService.removeUserFromRoom(roomId, principal.getName());
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/room/{roomId}")
//    public ResponseEntity<Void> deleteRoom(@PathVariable String roomId, Principal principal) {
//        try {
//            chatService.deleteRoom(roomId, principal.getName());
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            if (e.getMessage().contains("방장만")) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//            }
//            return ResponseEntity.notFound().build();
//        }
//    }
}