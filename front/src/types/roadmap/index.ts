export interface RoadmapRequest {
  mainCategory: string; // 중분류
  subCategory: string; // 주제
  currentLevel?: string; // 1(초급) 2(중급) 3(고급)
  duration: number; // 주 단위
}

// 일별 계획 인터페이스
export interface DailyPlan {
  day: number; // 날짜
  dailyKeyword: string; // 일별 키워드
}

// 주차별 계획 인터페이스
export interface WeeklyPlan {
  week: number; // 주차
  weeklyKeyword: string; // 주차별 키워드
  dailyPlans: DailyPlan[]; // 일별 계획
}

// 로드맵 응답 인터페이스
export interface WeeklyRoadmapResponse {
  weeklyPlans: WeeklyPlan[]; // 주차별 계획
  overallTips: string[]; // 전체 팁
  curriculumEvaluation: string; // 커리큘럼 평가
  hasRestrictedTopics: string; // 금지된 주제 여부
}

// 로드맵 심플 DTO 인터페이스
export interface RoadMapSimpleDto {
  id: number; // Long -> number
  topic: string;
}
