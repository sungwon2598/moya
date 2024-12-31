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

// Mock data
const MOCK_STUDY_POSTS: StudyPost[] = [
    {
        postId: 1,
        title: "[Front] 소상공인 급여 정산 자동화 스케줄링 프로그램을 함께 제작할 팀...",
        content: "프론트엔드 개발자를 모집합니다...",
        recruits: 4,
        expectedPeriod: "3개월",
        startDate: "2024-12-23",
        endDate: "2025-03-23",
        studies: "Programming Languages",
        studyDetails: "Java",
        authorName: "구렁",
        views: 130,
        totalComments: 0,
        isLiked: false,
        tags: ["React", "TypeScript", "Next.js"]
    },
    {
        postId: 2,
        title: "식당 운영 서비스 개발 팀원 모집",
        content: "백엔드 개발자를 모집합니다...",
        recruits: 3,
        expectedPeriod: "4개월",
        startDate: "2024-12-23",
        endDate: "2025-04-23",
        studies: "클라우드 서비스",
        studyDetails: "Python",
        authorName: "nearworld",
        views: 91,
        totalComments: 0,
        isLiked: true,
        tags: ["Spring", "JPA", "AWS"]
    },
    {
        postId: 3,
        title: "취업용 프로젝트 멤버 구합니다(FE 1월 마무리 예정)",
        content: "취준생들과 함께할 프로젝트입니다...",
        recruits: 4,
        expectedPeriod: "1개월",
        startDate: "2024-12-07",
        endDate: "2025-01-14",
        studies: "AWS",
        studyDetails: "",
        authorName: "ts",
        views: 28,
        totalComments: 0,
        isLiked: false,
        tags: ["TypeScript", "React"]
    },
    {
        postId: 4,
        title: "LLM을 활용한 회고 관리 서비스에서 백엔드 개발자를 찾고 있습니다!",
        content: "AI 기술을 활용한 서비스 개발...",
        recruits: 2,
        expectedPeriod: "3개월",
        startDate: "2024-11-31",
        endDate: "2025-12-06",
        studies: "Linux",
        studyDetails: "",
        authorName: "팀오디세이",
        views: 77,
        totalComments: 0,
        isLiked: true,
        tags: ["Spring", "AWS", "Docker", "Jenkins"]
    },
    {
        postId: 5,
        title: "[모집 마감 D-5] 재직자는 100% 무료 데이터 스킬업 프로젝트",
        content: "데이터 분석 스터디...",
        recruits: 5,
        expectedPeriod: "2개월",
        startDate: "2024-12-13",
        endDate: "2025-02-13",
        studies: "Azure",
        studyDetails: "",
        authorName: "팀스파르타1",
        views: 23,
        totalComments: 0,
        isLiked: false,
        tags: ["Python", "Pandas", "SQL"]
    }
];

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
    // 카테고리 계층 구조 조회 (실제 API 호출)
    getCategoriesHierarchy: async (): Promise<Category[]> => {
        const response = await axios.get<Category[]>(
            STUDY_ENDPOINTS.CATEGORIES_HIERARCHY
        );
        return response.data;  // 직접 Category[] 반환
    },

    createPost: async (postData: CreateStudyDTO): Promise<StudyApiResponse<StudyPost>> => {
        try {
            // Create a mock response instead of making an API call
            const mockPost: StudyPost = {
                postId: MOCK_STUDY_POSTS.length + 1, // Generate new ID
                ...postData,
                authorName: "테스트 유저",  // Mock user
                views: 0,
                totalComments: 0,
                isLiked: false,
                tags: []  // Initialize empty tags
            };

            // Add to mock posts array
            MOCK_STUDY_POSTS.push(mockPost);

            // Return mock response
            return {
                data: mockPost,
                meta: { status: 200 }
            };
        } catch (error) {
            console.error('Error creating post:', error);
            throw error;
        }
    },

    // 스터디 목록 조회 (Mock 데이터 사용)
    getStudyList: async (page = 0, size = 10, filters?: Record<string, any>): Promise<StudyApiResponse<StudyPost[]>> => {
        let filteredPosts = [...MOCK_STUDY_POSTS];

        if (filters?.studies) {
            filteredPosts = filteredPosts.filter(post =>
                post.studies === filters.studies
            );
        }

        if (filters?.studyDetails) {
            filteredPosts = filteredPosts.filter(post =>
                post.studyDetails === filters.studyDetails
            );
        }

        if (filters?.techStack) {
            filteredPosts = filteredPosts.filter(post =>
                post.tags?.some(tag =>
                    tag.toLowerCase().includes(filters.techStack.toLowerCase())
                )
            );
        }

        if (filters?.progressType) {
            // 추후 progressType 필드가 추가되면 구현
        }

        const start = page * size;
        const end = start + size;
        const paginatedPosts = filteredPosts.slice(start, end);

        return {
            data: paginatedPosts,
            meta: {
                status: 200,
                pagination: {
                    page,
                    size,
                    totalElements: filteredPosts.length,
                    totalPages: Math.ceil(filteredPosts.length / size),
                    first: page === 0,
                    last: page >= Math.ceil(filteredPosts.length / size) - 1
                }
            }
        };
    },

    // 스터디 상세 조회 (Mock 데이터 사용)
    getStudyDetail: async (postId: number): Promise<StudyApiResponse<StudyPost>> => {
        const post = MOCK_STUDY_POSTS.find(post => post.postId === postId);

        if (!post) {
            throw new Error('게시글을 찾을 수 없습니다.');
        }

        return {
            data: { ...post },
            meta: { status: 200 }
        };
    },

    // 좋아요 관련 메서드들 (Mock 데이터 사용)
    addLike: async (postId: number): Promise<StudyApiResponse<void>> => {
        const postIndex = MOCK_STUDY_POSTS.findIndex(post => post.postId === postId);

        if (postIndex === -1) {
            throw new Error('게시글을 찾을 수 없습니다.');
        }

        MOCK_STUDY_POSTS[postIndex].isLiked = true;

        return {
            data: undefined,
            meta: { status: 200 }
        };
    },

    removeLike: async (postId: number): Promise<StudyApiResponse<void>> => {
        const postIndex = MOCK_STUDY_POSTS.findIndex(post => post.postId === postId);

        if (postIndex === -1) {
            throw new Error('게시글을 찾을 수 없습니다.');
        }

        MOCK_STUDY_POSTS[postIndex].isLiked = false;

        return {
            data: undefined,
            meta: { status: 200 }
        };
    }
};

export default studyApiService;
