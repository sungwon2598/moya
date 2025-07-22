package com.study.moya.admin.dto.roadmap.response;

import com.study.moya.ai_roadmap.domain.RoadMap;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "어드민 - 로드맵 상세 정보 응답")
public class AdminRoadmapDetailResponse {

    @Schema(description = "로드맵 ID")
    private Long id;

    @Schema(description = "주제")
    private String topic;

    @Schema(description = "목표 레벨")
    private Integer goalLevel;

    @Schema(description = "학습 기간(주)")
    private Integer duration;

    @Schema(description = "학습 목표")
    private String learningObjective;

    @Schema(description = "평가")
    private String evaluation;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "대분류(Etc1)")
    private String etc1Name;

    @Schema(description = "중분류(Etc2)")
    private String etc2Name;

    @Schema(description = "전체 팁")
    private List<String> overallTips;

    @Schema(description = "구독자 수")
    private Long subscriberCount;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime modifiedAt;

    public static AdminRoadmapDetailResponse from(RoadMap roadMap, Long subscriberCount) {
        return AdminRoadmapDetailResponse.builder()
                .id(roadMap.getId())
                .topic(roadMap.getTopic())
                .goalLevel(roadMap.getGoalLevel())
                .duration(roadMap.getDuration())
                .learningObjective(roadMap.getLearningObjective())
                .evaluation(roadMap.getEvaluation())
                .categoryName(roadMap.getCategory() != null ? roadMap.getCategory().getName() : null)
                .etc1Name(roadMap.getEtc1() != null ? roadMap.getEtc1().getName() : null)
                .etc2Name(roadMap.getEtc2() != null ? roadMap.getEtc2().getName() : null)
                .overallTips(roadMap.getOverallTips())
                .subscriberCount(subscriberCount)
                .createdAt(roadMap.getCreatedAt())
                .modifiedAt(roadMap.getModifiedAt())
                .build();
    }
}
