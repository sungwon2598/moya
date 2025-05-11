package com.study.moya.ai_roadmap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorksheetStatusResponse {
    private boolean completed;
    private long progress;
    private String message;
}
