package com.study.moya.chat;

import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.chat.text.dto.chat.ChatDTO;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import com.study.moya.chat.message.ChatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ChatReliabilityTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    private static final String SUBSCRIBE_ENDPOINT = "/sub/chat/room/";
    private static final String PUBLISH_ENDPOINT = "/pub/chat/message";
    private static final int MESSAGE_COUNT = 1000;
    private static final int CONCURRENT_CLIENTS = 5;
    private static final int ROOMS_COUNT = 3;
    private static final int MESSAGE_DELAY_MS = 50;

    private String testUserToken;

    @BeforeEach
    void setup() {
        // 테스트용 사용자 설정
        Member testMember = memberRepository.findByEmail("test5@example.com")
                .orElseGet(() -> {
                    Member member = Member.builder()
                            .email("test@test.com")
                            .password("test1234")
                            .nickname("테스트유저")
                            .build();
                    return memberRepository.save(member);
                });

        // Authentication 객체 생성
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(testMember, null, authorities);

        // 토큰 생성
        JwtTokenProvider.TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);
        testUserToken = tokenInfo.getAccessToken();

        log.info("Test user token created: {}", testUserToken);
    }

    @Test
    void reliabilityTest() throws Exception {
        String wsUrl = "ws://localhost:" + port + "/ws-stomp";
        log.info("Starting WebSocket reliability test on URL: {}", wsUrl);

        // SockJS 클라이언트 설정
        List<Transport> transports = Collections.singletonList(
                new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // 결과 저장용 자료구조
        ConcurrentHashMap<String, CopyOnWriteArrayList<ChatDTO>> sentMessages = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, CopyOnWriteArrayList<ChatDTO>> receivedMessages = new ConcurrentHashMap<>();
        CountDownLatch completionLatch = new CountDownLatch(CONCURRENT_CLIENTS);
        AtomicInteger totalMessageCounter = new AtomicInteger(0);

        // 클라이언트 세션 관리
        List<StompSession> sessions = new ArrayList<>();
        try {
            // 클라이언트 연결 설정
            for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
                String clientId = "client-" + i;
                StompSession session = connectStompClient(stompClient, clientId, wsUrl);
                sessions.add(session);

                // 각 방에 대한 구독 설정
                for (int roomId = 0; roomId < ROOMS_COUNT; roomId++) {
                    String roomIdentifier = String.valueOf(roomId);
                    sentMessages.putIfAbsent(roomIdentifier, new CopyOnWriteArrayList<>());
                    receivedMessages.putIfAbsent(roomIdentifier, new CopyOnWriteArrayList<>());

                    subscribeToRoom(session, SUBSCRIBE_ENDPOINT + roomId,
                            receivedMessages, clientId, roomIdentifier);
                }

                log.info("Client {} successfully connected and subscribed to all rooms", clientId);
            }

            // 메시지 전송 시작
            log.info("Starting message transmission with {} clients", CONCURRENT_CLIENTS);
            ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_CLIENTS);
            List<Future<?>> futures = new ArrayList<>();

            for (StompSession session : sessions) {
                futures.add(executorService.submit(() -> {
                    try {
                        sendTestMessages(session, sentMessages, totalMessageCounter);
                    } finally {
                        completionLatch.countDown();
                    }
                }));
            }

            // 전송 완료 대기
            if (!completionLatch.await(5, TimeUnit.MINUTES)) {
                log.error("Test timed out waiting for message transmission");
                throw new RuntimeException("Test timed out");
            }

            // 메시지 수신 대기
            log.info("Waiting for message processing completion...");
            Thread.sleep(5000);

            // 결과 검증
            verifyTestResults(sentMessages, receivedMessages);

        } finally {
            // 리소스 정리
            sessions.forEach(session -> {
                try {
                    if (session.isConnected()) {
                        session.disconnect();
                    }
                } catch (Exception e) {
                    log.error("Error disconnecting session", e);
                }
            });
        }
    }

    private StompSession connectStompClient(
            WebSocketStompClient stompClient,
            String clientId,
            String wsUrl) throws Exception {

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + testUserToken);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "Bearer " + testUserToken);

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void handleException(StompSession session,
                                        StompCommand command,
                                        StompHeaders headers,
                                        byte[] payload,
                                        Throwable exception) {
                log.error("Client {} connection error", clientId, exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("Transport error for client {}", clientId, exception);
            }

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("Client {} connected successfully", clientId);
            }
        };

        return stompClient
                .connect(wsUrl, headers, stompHeaders, sessionHandler)
                .get(10, TimeUnit.SECONDS);
    }

    private void sendTestMessages(StompSession session,
                                  ConcurrentHashMap<String, CopyOnWriteArrayList<ChatDTO>> sentMessages,
                                  AtomicInteger messageCounter) {
        try {
            for (int i = 0; i < MESSAGE_COUNT / CONCURRENT_CLIENTS; i++) {
                int roomId = i % ROOMS_COUNT;
                String roomIdentifier = String.valueOf(roomId);
                int sequenceNumber = messageCounter.getAndIncrement();

                ChatDTO message = new ChatDTO(
                        ChatType.CHAT,
                        roomIdentifier,
                        "test@test.com",  // 실제 테스트 유저의 이메일을 사용
                        "Message " + sequenceNumber,
                        LocalDateTime.now()
                );

                sentMessages.get(roomIdentifier).add(message);
                session.send(PUBLISH_ENDPOINT, message);

                if (i % 100 == 0) {
                    log.info("Sent {} messages to room {}", i, roomId);
                }

                Thread.sleep(MESSAGE_DELAY_MS);
            }
        } catch (Exception e) {
            log.error("Error sending messages", e);
            throw new RuntimeException(e);
        }
    }

    private void subscribeToRoom(
            StompSession session,
            String roomPath,
            ConcurrentHashMap<String, CopyOnWriteArrayList<ChatDTO>> receivedMessages,
            String clientId,
            String roomId) {

        session.subscribe(roomPath, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ChatDTO message = (ChatDTO) payload;
                receivedMessages.get(roomId).add(message);

                int messageCount = receivedMessages.get(roomId).size();
                if (messageCount % 100 == 0) {
                    log.info("Client {} received {} messages in room {}",
                            clientId, messageCount, roomId);
                }
            }
        });

        log.info("Client {} subscribed to room {}", clientId, roomId);
    }

    private void verifyTestResults(
            ConcurrentHashMap<String, CopyOnWriteArrayList<ChatDTO>> sentMessages,
            ConcurrentHashMap<String, CopyOnWriteArrayList<ChatDTO>> receivedMessages) {

        sentMessages.forEach((roomId, sent) -> {
            CopyOnWriteArrayList<ChatDTO> received = receivedMessages.get(roomId);

            log.info("Room {} - Sent: {}, Received: {}",
                    roomId, sent.size(), received.size());

            boolean ordered = verifyMessageOrder(received);
            log.info("Room {} message order maintained: {}", roomId, ordered);

            Set<String> uniqueMessages = new HashSet<>();
            long duplicates = received.stream()
                    .map(ChatDTO::message)
                    .filter(msg -> !uniqueMessages.add(msg))
                    .count();
            log.info("Room {} duplicate messages: {}", roomId, duplicates);

            Set<String> sentContents = sent.stream()
                    .map(ChatDTO::message)
                    .collect(Collectors.toSet());
            Set<String> receivedContents = received.stream()
                    .map(ChatDTO::message)
                    .collect(Collectors.toSet());

            Set<String> missing = new HashSet<>(sentContents);
            missing.removeAll(receivedContents);

            if (!missing.isEmpty()) {
                log.error("Room {} missing messages: {}", roomId, missing.size());
                log.error("First few missing messages: {}",
                        missing.stream().limit(5).collect(Collectors.toList()));
            }
        });
    }

    private boolean verifyMessageOrder(List<ChatDTO> messages) {
        if (messages.size() < 2) return true;

        for (int i = 1; i < messages.size(); i++) {
            LocalDateTime prev = messages.get(i-1).timestamp();
            LocalDateTime curr = messages.get(i).timestamp();
            if (prev.isAfter(curr)) {
                log.error("Message order violation at index {}: {} after {}",
                        i, prev, curr);
                return false;
            }
        }
        return true;
    }
}