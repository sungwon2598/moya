package com.study.moya.ai_roadmap.constants;

public enum LearningObjective {
    BASIC_UNDERSTANDING("기본 개념 이해"),
    PRACTICAL_SKILLS("실무 능력 향상"),
    CERTIFICATION("자격증 취득"),
    PORTFOLIO("포트폴리오 구축"),
    INTERVIEW_PREP("면접 준비"),
    ADVANCED_SKILLS("고급 기술 습득");

    private final String description;

    LearningObjective(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
