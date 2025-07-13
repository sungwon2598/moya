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

    // Projection에서 DTO로 변환하는 정적 메서드 추가
    public static RoadMapSummaryDTO from(RoadMapSummaryProjection projection) {
        return new RoadMapSummaryDTO(
                projection.getId(),
                projection.getMainCategory(),
                projection.getSubCategory(),
                projection.getDuration()
        );
    }
}