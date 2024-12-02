package com.study.moya.chat.message;

public enum SystemMessageType {
    JOIN("님이 입장하였습니다."),
    LEAVE("님이 나갔습니다.");

    private final String messageTemplate;

    SystemMessageType(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String generateMessage(String username) {
        return username + this.messageTemplate;
    }
}