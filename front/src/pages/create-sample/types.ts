// Roadmap Request Types
export interface RoadmapRequest {
    mainCategory: string;    // 중분류 (예: "프로그래밍 언어")
    subCategory: string;     // 소분류/주제 (예: "자바")
    currentLevel: string;    // 목표 레벨 (1: 초급, 2: 중급, 3: 고급)
    duration: number;        // 학습 기간 (주 단위)
}

// Roadmap Response Types
export interface DailyPlan {
    day: number;
    dailyKeyword: string;
}

export interface WeeklyPlan {
    week: number;
    weeklyKeyword: string;
    dailyPlans: DailyPlan[];
}

export interface RoadmapResponse {
    weeklyPlans: WeeklyPlan[];
    overallTips: string[];
    curriculumEvaluation: string;
    hasRestrictedTopics: string;
}