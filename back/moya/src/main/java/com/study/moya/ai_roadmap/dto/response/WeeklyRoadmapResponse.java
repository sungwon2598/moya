package com.study.moya.ai_roadmap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WeeklyRoadmapResponse {

    private List<WeeklyPlan> weeklyPlans; // 주차별 계획
    private List<String> overallTips; // 전체 팁
    private String curriculumEvaluation; // 커리큘럼 평가
    private String hasRestrictedTopics; // 금지된 주제 여부

    @Getter
    @Setter
    @AllArgsConstructor
    public static class WeeklyPlan {
        private int week; // 주차
        private String weeklyKeyword; // 주차별 키워드
        private List<DailyPlan> dailyPlans; // 일별 계획
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DailyPlan {
        private int day; // 날짜
        private String dailyKeyword; // 일별 키워드
    }
}