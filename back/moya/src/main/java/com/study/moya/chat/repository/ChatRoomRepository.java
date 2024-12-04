package com.study.moya.chat.repository;

import com.study.moya.chat.domain.ChatRoom;
import com.study.moya.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomId(String roomId);
    List<ChatRoom> findByCreator(Member creator);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.chatRoomMembers crm WHERE crm.member = :member")
    List<ChatRoom> findByMember(@Param("member") Member member);

}