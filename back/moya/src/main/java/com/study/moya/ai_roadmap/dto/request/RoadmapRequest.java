package com.study.moya.ai_roadmap.dto.request;

import com.study.moya.ai_roadmap.constants.LearningObjective;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoadmapRequest {

    @NotBlank(message = "Main category is required")
    private String mainCategory; // 중분류

    @NotBlank(message = "Subcategory is required")
    private String subCategory; // 주제

    private String currentLevel; // 현재 수준 : 1(초급) 2(중급) 3(고급)

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 week")
    private Integer duration; // 주 단위

    private LearningObjective learningObjective; // 학습목표

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
                '}';
    }
}