package com.study.moya.chat.message.hadnler;

import com.study.moya.chat.text.dto.chat.ChatDTO;

public interface MessageHandler {
    void handle(ChatDTO chatDTO, String userEmail);
    boolean supports(ChatDTO chatDTO);
}