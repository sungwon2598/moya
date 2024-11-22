package com.study.moya.chat.text.dto.chatroom;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roomId;
    private String roomName;
    private long userCount;
    private ChatRoomType type;
    private Map<String, String> userList;

    public ChatRoomDTO() {
        this.roomId = UUID.randomUUID().toString();
        this.userList = new ConcurrentHashMap<>();
        this.userCount = 0;
    }

    public ChatRoomDTO(String roomName, ChatRoomType type) {
        this();
        this.roomName = roomName;
        this.type = type;
    }

    public void addUser(String userId, String userName) {
        if (userList.putIfAbsent(userId, userName) == null) {
            userCount++;
        }
    }

    public void removeUser(String userId) {
        if (userList.remove(userId) != null) {
            userCount--;
        }
    }
}