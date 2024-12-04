package com.study.moya.chat.text.service;

import com.study.moya.chat.domain.ChatRoom;
import com.study.moya.chat.domain.RedisChatMessage;
import com.study.moya.chat.repository.ChatMessageRepository;
import com.study.moya.chat.repository.ChatRoomMemberRepository;
import com.study.moya.chat.repository.ChatRoomRepository;
import com.study.moya.chat.text.dto.chat.ChatDTO;
import com.study.moya.chat.text.dto.chat.ChatMessageResponse;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private static final int DEFAULT_CHAT_LOAD_SIZE = 50;
    private final RedisTemplate<String, RedisChatMessage> chatMessageRedisTemplate;
    private final ChatMessageRepository chatMessageRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    private static final String CHAT_MESSAGES_KEY = "CHAT_MESSAGES:";
    private static final Duration CHAT_MESSAGE_EXPIRE_TIME = Duration.ofDays(7);

    public ChatRoom createTextRoom(String roomName, String creatorEmail) {
        Member creator = memberRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .creator(creator)
                .build();

        chatRoom.addMember(creator);
        return chatRoomRepository.save(chatRoom);
    }

    public void addUserToRoom(String roomId, String userEmail) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (!chatRoomMemberRepository.existsByChatRoomAndMember(chatRoom, member)) {
            chatRoom.addMember(member);
            chatRoomRepository.save(chatRoom);
        }
    }

    public void removeUserFromRoom(String roomId, String userEmail) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        chatRoom.removeMember(member);
        chatRoomRepository.save(chatRoom);
    }

    public void saveMessage(ChatDTO chatDTO) {
        RedisChatMessage redisMessage = convertToRedisMessage(chatDTO);
        String messageKey = CHAT_MESSAGES_KEY + chatDTO.roomId();

        chatMessageRedisTemplate.opsForList().rightPush(messageKey, redisMessage);
        chatMessageRedisTemplate.expire(messageKey, CHAT_MESSAGE_EXPIRE_TIME);

        log.debug("Saved chat message to Redis - Room: {}, Sender: {}",
                chatDTO.roomId(), chatDTO.sender());
    }

    public List<ChatRoom> getUserChatRooms(String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return chatRoomRepository.findByMember(member);
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId));
    }

    public boolean isRoomMember(String roomId, String userEmail) {
        ChatRoom chatRoom = findRoomById(roomId);
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return chatRoomMemberRepository.existsByChatRoomAndMember(chatRoom, member);
    }

    public void deleteRoom(String roomId, String userEmail) {
        ChatRoom chatRoom = findRoomById(roomId);
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (!chatRoom.getCreator().equals(member)) {
            throw new IllegalArgumentException("방장만 채팅방을 삭제할 수 있습니다.");
        }

        // Redis에서 채팅 메시지 삭제
        String messageKey = CHAT_MESSAGES_KEY + roomId;
        chatMessageRedisTemplate.delete(messageKey);

        // DB에서 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }

    // 채팅 메시지 관련 메서드들은 그대로 유지
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

    public List<ChatMessageResponse> loadChatHistory(String roomId, String userEmail, LocalDateTime before) {
        // 권한 체크
        if (!isRoomMember(roomId, userEmail)) {
            throw new IllegalArgumentException("채팅방의 멤버가 아닙니다.");
        }

        // 첫 로딩이면 가장 최근 메시지부터
        if (before == null) {
            return chatMessageRepository.findByRoomIdOrderByTimestampDesc(
                            roomId,
                            PageRequest.of(0, DEFAULT_CHAT_LOAD_SIZE)
                    )
                    .stream()
                    .map(ChatMessageResponse::new)
                    .collect(Collectors.toList());
        }

        // 특정 시점 이전의 메시지 로딩 (페이지네이션)
        return chatMessageRepository.findByRoomIdAndTimestampBeforeOrderByTimestampDesc(
                        roomId,
                        before,
                        PageRequest.of(0, DEFAULT_CHAT_LOAD_SIZE)
                )
                .stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());
    }
}