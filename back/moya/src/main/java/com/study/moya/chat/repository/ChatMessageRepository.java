package com.study.moya.chat.repository;

import com.study.moya.chat.domain.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId ORDER BY cm.timestamp DESC")
    List<ChatMessage> findByRoomIdOrderByTimestampDesc(@Param("roomId") String roomId, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.timestamp < :timestamp ORDER BY cm.timestamp DESC")
    List<ChatMessage> findByRoomIdAndTimestampBeforeOrderByTimestampDesc(
            @Param("roomId") String roomId,
            @Param("timestamp") LocalDateTime timestamp,
            Pageable pageable
    );

}