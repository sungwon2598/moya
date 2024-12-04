package com.study.moya.chat.text.dto.chatroom;


import com.study.moya.chat.domain.ChatRoom;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class ChatRoomResponse {
    private Long id;
    private String roomId;
    private String name;
    private String creator;
    private List<String> members;

    public ChatRoomResponse(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.roomId = chatRoom.getRoomId();
        this.name = chatRoom.getName();
        this.creator = chatRoom.getCreator().getEmail();
        this.members = chatRoom.getChatRoomMembers().stream()
                .map(member -> member.getMember().getEmail())
                .collect(Collectors.toList());
    }
}