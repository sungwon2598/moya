import axios from 'axios';

const BASE_URL = 'https://api.moyastudy.com';

// 스터디 관련 엔드포인트 정의
export const STUDY_ENDPOINTS = {
    LIST: `${BASE_URL}/posts`,
    DETAIL: (postId: number) => `${BASE_URL}/posts/${postId}`,
    CREATE: `${BASE_URL}/posts`,
    UPDATE: (postId: number) => `${BASE_URL}/posts/${postId}`,
    DELETE: (postId: number) => `${BASE_URL}/posts/${postId}`,
    LIKE: (postId: number) => `${BASE_URL}/posts/${postId}/like`,
    CATEGORIES_HIERARCHY: `${BASE_URL}/api/categories/hierarchy`,
} as const;

// 카테고리 관련 타입 정의
export interface Category {
    id: number;
    name: string;
    subCategories: Category[];
}

// 스터디 게시글 관련 타입 정의
export interface StudyPost {
    postId: number;
    title: string;
    content: string;
    recruits: number;
    expectedPeriod: string;
    startDate: string;
    endDate: string;
    studies: string;
    studyDetails: string;
    authorName: string;
    views: number;
    totalComments: number;
    isLiked: boolean;
    tags?: string[];
}

export interface CreateStudyDTO {
    title: string;
    content: string;
    recruits: number;
    expectedPeriod: string;
    startDate: string;
    endDate: string;
    studies: string;
    studyDetails: string;
}

export interface UpdateStudyDTO extends CreateStudyDTO {
    postId: number;
}

export interface StudyApiResponse<T> {
    data: T;
    meta: {
        status: number;
        pagination?: {
            page: number;
            size: number;
            totalElements: number;
            totalPages: number;
            first: boolean;
            last: boolean;
        };
    };
    error?: {
        code: string;
        message: string;
        details: Record<string, unknown>;
    };
}

// 스터디 API 서비스 객체
export const studyApiService = {
    // 카테고리 계층 구조 조회
    getCategoriesHierarchy: async () => {
        const response = await axios.get<StudyApiResponse<Category[]>>(
            STUDY_ENDPOINTS.CATEGORIES_HIERARCHY
        );
        return response.data;
    },

    // 스터디 목록 조회
    getStudyList: async (page = 0, size = 10, filters?: Record<string, string>) => {
        const response = await axios.get<StudyApiResponse<StudyPost[]>>(
            STUDY_ENDPOINTS.LIST,
            {
                params: {
                    page,
                    size,
                    ...filters,
                },
            }
        );
        return response.data;
    },

    // 스터디 상세 조회
    getStudyDetail: async (postId: number) => {
        const response = await axios.get<StudyApiResponse<StudyPost>>(
            STUDY_ENDPOINTS.DETAIL(postId)
        );
        return response.data;
    },

    // 스터디 생성
    createStudy: async (data: CreateStudyDTO) => {
        const response = await axios.post<StudyApiResponse<StudyPost>>(
            STUDY_ENDPOINTS.CREATE,
            data
        );
        return response.data;
    },

    // 스터디 수정
    updateStudy: async (postId: number, data: UpdateStudyDTO) => {
        const response = await axios.post<StudyApiResponse<StudyPost>>(
            STUDY_ENDPOINTS.UPDATE(postId),
            data
        );
        return response.data;
    },

    // 스터디 삭제
    deleteStudy: async (postId: number) => {
        const response = await axios.delete<StudyApiResponse<void>>(
            STUDY_ENDPOINTS.DELETE(postId)
        );
        return response.data;
    },

    // 좋아요 추가
    addLike: async (postId: number) => {
        const response = await axios.post<StudyApiResponse<void>>(
            STUDY_ENDPOINTS.LIKE(postId)
        );
        return response.data;
    },

    // 좋아요 취소
    removeLike: async (postId: number) => {
        const response = await axios.delete<StudyApiResponse<void>>(
            STUDY_ENDPOINTS.LIKE(postId)
        );
        return response.data;
    },

    // 에러 핸들링을 위한 유틸리티 함수
    handleApiError: (error: unknown) => {
        if (axios.isAxiosError(error)) {
            // API 에러 처리
            const errorResponse = error.response?.data;
            return {
                code: errorResponse?.error?.code || 'UNKNOWN_ERROR',
                message: errorResponse?.error?.message || '알 수 없는 오류가 발생했습니다.',
            };
        }
        // 일반 에러 처리
        return {
            code: 'UNKNOWN_ERROR',
            message: '알 수 없는 오류가 발생했습니다.',
        };
    }
};

export default studyApiService;
