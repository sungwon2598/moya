package com.study.moya.ai_roadmap.dto.response;

import lombok.Getter;

@Getter
public class RoadMapSimpleDto {
    private Long id;
    private String topic;

    public RoadMapSimpleDto(Long id, String topic) {
        this.id = id;
        this.topic = topic;
    }
}