package com.study.moya.ai_roadmap.dto.request;

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
    private String mainCategory; // 대분류

    @NotBlank(message = "Subcategory is required")
    private String subCategory; // 중분류

    private String currentLevel;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 week")
    private Integer duration; // 주 단위

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
                '}';
    }
}
