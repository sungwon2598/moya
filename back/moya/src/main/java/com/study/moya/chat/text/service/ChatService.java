package com.study.moya.chat.text.service;

import com.study.moya.chat.text.dto.chatroom.ChatRoomDTO;
import com.study.moya.chat.text.dto.chatroom.ChatRoomType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatService {
    //이 부분 redis->mysql 스케쥴링..?
    private Map<String, ChatRoomDTO> chatRoomMap;

    @PostConstruct
    private void init() {
        chatRoomMap = new ConcurrentHashMap<>();
    }

    // 채팅방 생성
    public ChatRoomDTO createTextRoom(String roomName) {
        ChatRoomDTO chatRoom = ChatRoomDTO.builder()
                .roomName(roomName)
                .type(ChatRoomType.TEXT)
                .build();

        // 기본적으로 모든 스터디원이 참여하도록 로직 추가
        // 현재 사용자 엔티티가 없으므로 임시로 모든 사용자 추가 로직 생략

        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        log.info("Created chat room: {}", chatRoom.getRoomId());

        return chatRoom;
    }

    // 채팅방 인원 +1 및 사용자 추가
    public void addUserToRoom(String roomId, String userName) {
        ChatRoomDTO chatRoom = chatRoomMap.get(roomId);
        if (chatRoom == null) {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId);
        }

        String userId = UUID.randomUUID().toString();
        chatRoom.addUser(userId, userName);
        log.info("Added user {} to chat room {}", userName, roomId);
    }

    // 채팅방 인원 -1 및 사용자 삭제
    public void removeUserFromRoom(String roomId, String userId) {
        ChatRoomDTO chatRoom = chatRoomMap.get(roomId);
        if (chatRoom == null) {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId);
        }

        chatRoom.removeUser(userId);
        log.info("Removed user {} from chat room {}", userId, roomId);
    }

    // 특정 채팅방 조회
    public ChatRoomDTO findRoomById(String roomId) {
        log.debug("Finding room by ID: {}", roomId);
        ChatRoomDTO chatRoom = chatRoomMap.get(roomId);
        if (chatRoom == null) {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId);
        }
        return chatRoom;
    }

    // 모든 채팅방 조회
    public List<ChatRoomDTO> findAllRooms() {
        return new ArrayList<>(chatRoomMap.values());
    }

    // 채팅방 삭제 (필요 시 추가)
    public void deleteRoom(String roomId) {
        if (chatRoomMap.remove(roomId) != null) {
            log.info("Deleted chat room: {}", roomId);
        } else {
            throw new IllegalArgumentException("삭제할 채팅방을 찾을 수 없습니다: " + roomId);
        }
    }
}