package com.study.moya.chat.text.service;

import com.study.moya.chat.domain.RedisChatMessage;
import com.study.moya.chat.text.dto.chat.ChatDTO;
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
    private final RedisTemplate<String, RedisChatMessage> chatMessageRedisTemplate;

    private static final String CHAT_ROOMS_KEY = "CHAT_ROOM";  // 채팅방 목록
    private static final String CHAT_ROOM_USERS_KEY = "CHAT_ROOM_USERS:";  // 채팅방 사용자
    private static final String CHAT_MESSAGES_KEY = "CHAT_MESSAGES:";  // 채팅 메시지 저장
    private static final long CHAT_ROOM_EXPIRE_TIME = 24 * 60 * 60; // 24시간
    private static final long CHAT_MESSAGE_EXPIRE_TIME = 7 * 24 * 60 * 60; // 7일

    public void saveMessage(ChatDTO chatDTO) {
        RedisChatMessage redisMessage = convertToRedisMessage(chatDTO);
        String messageKey = CHAT_MESSAGES_KEY + chatDTO.roomId();  // 채팅방별 키 구조

        chatMessageRedisTemplate.opsForList().rightPush(messageKey, redisMessage);
        chatMessageRedisTemplate.expire(messageKey, Duration.ofSeconds(CHAT_MESSAGE_EXPIRE_TIME));

        log.debug("Saved chat message to Redis - Room: {}, Sender: {}",
                chatDTO.roomId(), chatDTO.sender());
    }

    private RedisChatMessage convertToRedisMessage(ChatDTO chatDTO) {
        RedisChatMessage message = new RedisChatMessage();
        message.setRoomId(chatDTO.roomId());
        message.setSender(chatDTO.sender());
        message.setMessage(chatDTO.message());
        message.setTimestamp(chatDTO.timestamp());
        message.setType(chatDTO.type());
        message.setSystemMessageType(chatDTO.systemMessageType());
        message.setProcessed(false);
        return message;
    }

    public List<RedisChatMessage> getRecentMessages(String roomId, int limit) {
        String messageKey = CHAT_MESSAGES_KEY + roomId;
        List<RedisChatMessage> messages = chatMessageRedisTemplate.opsForList()
                .range(messageKey, -limit, -1);

        return messages != null ? messages : Collections.emptyList();
    }

    public ChatRoomDTO createTextRoom(String roomName, String creator) {
        ChatRoomDTO chatRoom = new ChatRoomDTO(roomName);
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
        String chatMessagesKey = CHAT_MESSAGES_KEY + roomId;

        HashOperations<String, String, ChatRoomDTO> hashOps = redisTemplate.opsForHash();

        Long deletedRooms = hashOps.delete(CHAT_ROOMS_KEY, roomId);
        redisTemplate.delete(roomUsersKey);
        redisTemplate.delete(chatMessagesKey);

        if (deletedRooms > 0) {
            log.info("Deleted chat room and related data from Redis: {}", roomId);
        } else {
            throw new IllegalArgumentException("삭제할 채팅방을 찾을 수 없습니다: " + roomId);
        }
    }
}