package com.study.moya.chat.text.dto.chatroom;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ChatRoomDTO {
    private final String roomId;
    private final String roomName;
    private long userCount;
    private final ChatRoomType type;
    private final Map<String, String> userList; // userId -> userName

    @Builder
    public ChatRoomDTO(String roomName, ChatRoomType type) {
        this.roomId = UUID.randomUUID().toString();
        this.roomName = roomName;
        this.userCount = 0;
        this.type = type;
        this.userList = new HashMap<>();
    }

    // 사용자 추가
    public void addUser(String userId, String userName) {
        if (userList.putIfAbsent(userId, userName) == null) {
            userCount++;
        }
    }

    // 사용자 삭제
    public void removeUser(String userId) {
        if (userList.remove(userId) != null) {
            userCount--;
        }
    }

//    // 엔티티를 DTO로 변환하는 정적 메서드
//    public static ChatRoomDTO fromModel(ChatRoom chatRoom) {
//        return ChatRoomDTO.builder()
//                .roomId(chatRoom.getRoomId())
//                .roomName(chatRoom.getRoomName())
//                .userCount(chatRoom.getUserCount())
//                .type(chatRoom.getType())
//                .userList(chatRoom.getUserList())
//                .build();
//    }
}