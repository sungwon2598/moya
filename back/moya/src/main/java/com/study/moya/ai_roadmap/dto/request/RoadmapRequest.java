package com.study.moya.ai_roadmap.dto.request;

import com.study.moya.ai_roadmap.constants.LearningObjective;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "로드맵 생성 요청")
public class RoadmapRequest {

    @Schema(description = "중분류", example = "프로그래밍 언어")
    private String mainCategory; // 중분류

    @Schema(description = "주제", example = "C#")
    private String subCategory; // 주제

    @Schema(description = "현재 수준 : 1(초급) 2(중급) 3(고급)", example = "1")
    private String currentLevel; // 현재 수준 : 1(초급) 2(중급)

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 week")
    @Schema(description = "학습 기간(주 단위)", example = "8")
    private Integer duration; // 주 단위

    @Schema(description = "학습 목표", example = "기본 개념 이해")
    private String learningObjective; // 학습목표

    private String etc1;

    private String etc2;

    // 기본 생성자
    public RoadmapRequest() {
    }

    // toString 메소드 오버라이드
    @Override
    public String toString() {
        return "RoadmapRequest{" +
                ", currentLevel='" + currentLevel + '\'' +
                ", mainCategory='" + mainCategory + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", duration=" + duration +
                ", learningObjective=" + learningObjective +
                ", etc1=" + etc1 +
                ", etc2=" + etc2 +
                '}';
    }
}