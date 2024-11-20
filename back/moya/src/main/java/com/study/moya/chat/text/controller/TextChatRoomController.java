package com.study.moya.chat.text.controller;

import com.study.moya.chat.text.dto.chatroom.ChatRoomDTO;
import com.study.moya.chat.text.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ws/chat")
public class TextChatRoomController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getAllRooms() {
        List<ChatRoomDTO> rooms = chatService.findAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/create")
    public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody String roomName) {
        ChatRoomDTO chatRoom = chatService.createTextRoom(roomName);

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
}
