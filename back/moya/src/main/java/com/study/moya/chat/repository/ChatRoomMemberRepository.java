package com.study.moya.chat.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.chat.domain.ChatRoom;
import com.study.moya.chat.domain.ChatRoomMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    Optional<ChatRoomMember> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
    List<ChatRoomMember> findByChatRoom(ChatRoom chatRoom);
    boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);

}