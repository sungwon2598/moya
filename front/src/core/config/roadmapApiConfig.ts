import axios from 'axios';

const BASE_URL = 'https://api.moyastudy.com';

// 로드맵 관련 엔드포인트 정의
export const ROADMAP_ENDPOINTS = {
    GENERATE: `${BASE_URL}/api/roadmap/generate`,
    GENERATE_WORKSHEETS: (roadmapId: number) => `${BASE_URL}/api/roadmap/${roadmapId}/worksheets`,
    GET_CATEGORY_ROADMAPS: (categoryId: number) => `${BASE_URL}/api/roadmap/categories/${categoryId}/roadmaps`
} as const;

// 로드맵 요청 DTO 인터페이스
export interface RoadmapRequest {
    mainCategory: string;  // 중분류
    subCategory: string;   // 주제
    currentLevel?: string; // 1(초급) 2(중급) 3(고급)
    duration: number;      // 주 단위
}

// 일별 계획 인터페이스
export interface DailyPlan {
    day: number;           // 날짜
    dailyKeyword: string;  // 일별 키워드
}

// 주차별 계획 인터페이스
export interface WeeklyPlan {
    week: number;          // 주차
    weeklyKeyword: string; // 주차별 키워드
    dailyPlans: DailyPlan[]; // 일별 계획
}

// 로드맵 응답 인터페이스
export interface WeeklyRoadmapResponse {
    weeklyPlans: WeeklyPlan[];      // 주차별 계획
    overallTips: string[];          // 전체 팁
    curriculumEvaluation: string;   // 커리큘럼 평가
    hasRestrictedTopics: string;    // 금지된 주제 여부
}

// 로드맵 심플 DTO 인터페이스
export interface RoadMapSimpleDto {
    id: number;    // Long -> number
    topic: string;
}

// 로드맵 API 서비스 객체
export const roadmapApiService = {
    // 주간 로드맵 생성
    generateWeeklyRoadmap: async (request: RoadmapRequest): Promise<WeeklyRoadmapResponse> => {
        try {
            const response = await axios.post<WeeklyRoadmapResponse>(
                ROADMAP_ENDPOINTS.GENERATE,
                request
            );
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error)) {
                throw new Error(error.response?.data?.message || '로드맵 생성에 실패했습니다.');
            }
            throw error;
        }
    },

    // 학습지 생성
    generateWorksheets: async (roadmapId: number): Promise<void> => {
        try {
            await axios.post(
                ROADMAP_ENDPOINTS.GENERATE_WORKSHEETS(roadmapId)
            );
        } catch (error) {
            if (axios.isAxiosError(error)) {
                throw new Error(error.response?.data?.message || '학습지 생성에 실패했습니다.');
            }
            throw error;
        }
    },

    // 카테고리별 로드맵 목록 조회
    getCategoryRoadmaps: async (categoryId: number): Promise<RoadMapSimpleDto[]> => {
        try {
            const response = await axios.get<RoadMapSimpleDto[]>(
                ROADMAP_ENDPOINTS.GET_CATEGORY_ROADMAPS(categoryId)
            );
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error)) {
                throw new Error(error.response?.data?.message || '카테고리별 로드맵 목록을 불러오는데 실패했습니다.');
            }
            throw error;
        }
    }
};

export default roadmapApiService;