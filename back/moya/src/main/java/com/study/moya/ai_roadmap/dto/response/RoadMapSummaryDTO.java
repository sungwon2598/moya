package com.study.moya.ai_roadmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로드맵 요약 정보")
public class RoadMapSummaryDTO {

    @Schema(description = "로드맵 ID", example = "1")
    private Long id;

    @Schema(description = "대분류 카테고리", example = "프로그래밍")
    private String mainCategory;

    @Schema(description = "중분류/주제", example = "웹 개발")
    private String subCategory;

    @Schema(description = "학습 기간(주)", example = "8")
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