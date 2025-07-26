package com.study.moya.ai_roadmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "주간 로드맵 응답")
public class WeeklyRoadmapResponse {

    @Schema(description = "주차별 계획 목록")
    private List<WeeklyPlan> weeklyPlans;

    @Schema(description = "전체 팁 목록", example = "[\"매일 꾸준히 학습하세요\", \"실습 위주로 진행하세요\"]")
    private List<String> overallTips;

    @Schema(description = "커리큘럼 평가", example = "이 커리큘럼은 초급자에게 적합하며, 실무 적용이 가능한 수준입니다.")
    private String curriculumEvaluation;

    @Schema(description = "금지된 주제 포함 여부", example = "없음")
    private String hasRestrictedTopics;

    @Getter
    @Setter
    @AllArgsConstructor
    @Schema(description = "주차별 계획")
    public static class WeeklyPlan {

        @Schema(description = "주차 번호", example = "1")
        private int week;

        @Schema(description = "주차별 키워드", example = "Java 기초 문법")
        private String weeklyKeyword;

        @Schema(description = "일별 계획 목록")
        private List<DailyPlan> dailyPlans;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Schema(description = "일별 계획")
    public static class DailyPlan {

        @Schema(description = "일차 번호", example = "1")
        private int day;

        @Schema(description = "일별 키워드", example = "변수와 데이터 타입")
        private String dailyKeyword;

        @Schema(description = "워크시트 내용", example = "변수는 똥물이 아닙니다.")
        private String worksheet;
    }
}