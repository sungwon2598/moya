package com.study.moya.ai_roadmap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoadMapSummaryDTO {
    private Long id;
    private String mainCategory;
    private String subCategory;
    private int duration;
}
