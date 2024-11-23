package com.study.moya.chat.text.service;

import com.study.moya.chat.text.dto.chatroom.ChatRoomDTO;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CHAT_ROOMS_KEY = "CHAT_ROOM";  // 채팅방 목록
    private static final String CHAT_ROOM_USERS_KEY = "CHAT_ROOM_USERS:";  // 채팅방 사용자
    private static final long CHAT_ROOM_EXPIRE_TIME = 24 * 60 * 60; // 24시간

    public ChatRoomDTO createTextRoom(String roomName, String creator) {
        ChatRoomDTO chatRoom = new ChatRoomDTO(roomName);  // 생성자 사용
        chatRoom.setCreator(creator);

        HashOperations<String, String, ChatRoomDTO> hashOps = redisTemplate.opsForHash();
        hashOps.put(CHAT_ROOMS_KEY, chatRoom.getRoomId(), chatRoom);

        redisTemplate.expire(CHAT_ROOMS_KEY, Duration.ofSeconds(CHAT_ROOM_EXPIRE_TIME));

        log.info("Created chat room in Redis: {}", chatRoom.getRoomId());
        return chatRoom;
    }

    public void addUserToRoom(String roomId, String userEmail) {
        String roomUsersKey = CHAT_ROOM_USERS_KEY + roomId;
        HashOperations<String, String, ChatRoomDTO> hashOps = redisTemplate.opsForHash();

        ChatRoomDTO room = hashOps.get(CHAT_ROOMS_KEY, roomId);
        if (room == null) {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId);
        }

        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        setOps.add(roomUsersKey, userEmail);

        // 사용자 목록 유효기간 설정
        redisTemplate.expire(roomUsersKey, Duration.ofSeconds(CHAT_ROOM_EXPIRE_TIME));

        room.addUser(userEmail, userEmail);
        hashOps.put(CHAT_ROOMS_KEY, roomId, room);

        log.info("Added user {} to chat room {}", userEmail, roomId);
    }

    public void removeUserFromRoom(String roomId, String userEmail) {
        String roomUsersKey = CHAT_ROOM_USERS_KEY + roomId;
        HashOperations<String, String, ChatRoomDTO> hashOps = redisTemplate.opsForHash();

        ChatRoomDTO room = hashOps.get(CHAT_ROOMS_KEY, roomId);
        if (room == null) {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId);
        }

        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        setOps.remove(roomUsersKey, userEmail);

        room.removeUser(userEmail);
        hashOps.put(CHAT_ROOMS_KEY, roomId, room);

        log.info("Removed user {} from chat room {}", userEmail, roomId);
    }

    public ChatRoomDTO findRoomById(String roomId) {
        HashOperations<String, String, ChatRoomDTO> hashOps = redisTemplate.opsForHash();
        ChatRoomDTO room = hashOps.get(CHAT_ROOMS_KEY, roomId);

        if (room == null) {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId);
        }

        return room;
    }

    public List<ChatRoomDTO> findAllRooms() {
        HashOperations<String, String, ChatRoomDTO> hashOps = redisTemplate.opsForHash();
        return new ArrayList<>(hashOps.values(CHAT_ROOMS_KEY));
    }

    public Set<String> getRoomUsers(String roomId) {
        String roomUsersKey = CHAT_ROOM_USERS_KEY + roomId;
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        Set<Object> members = setOps.members(roomUsersKey);

        if (members == null) {
            return Collections.emptySet();
        }

        return members.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public void deleteRoom(String roomId) {
        String roomUsersKey = CHAT_ROOM_USERS_KEY + roomId;
        HashOperations<String, String, ChatRoomDTO> hashOps = redisTemplate.opsForHash();

        Long deletedRooms = hashOps.delete(CHAT_ROOMS_KEY, roomId);
        redisTemplate.delete(roomUsersKey);

        if (deletedRooms > 0) {
            log.info("Deleted chat room from Redis: {}", roomId);
        } else {
            throw new IllegalArgumentException("삭제할 채팅방을 찾을 수 없습니다: " + roomId);
        }
    }
}