import axios from 'axios';

const BASE_URL = 'https://api.moyastudy.com';

// 스터디 관련 엔드포인트 정의
export const STUDY_ENDPOINTS = {
  LIST: `${BASE_URL}/api/posts?page=0`,
  DETAIL: (postId: number) => `${BASE_URL}/api/posts/${postId}`,
  CREATE: `${BASE_URL}/api/posts`,
  UPDATE: (postId: number) => `${BASE_URL}/api/posts/${postId}`,
  DELETE: (postId: number) => `${BASE_URL}/api/posts/${postId}`,
  LIKE: (postId: number) => `${BASE_URL}/api/posts/${postId}/like`,
  UNLIKE: (postId: number) => `${BASE_URL}/api/posts/${postId}/like`,
  CATEGORIES_HIERARCHY: `${BASE_URL}/api/categories/hierarchy`,
  CREATE_CATEGORY: `${BASE_URL}/api/categories`,
  DELETE_CATEGORY: (categoryId: number) => `${BASE_URL}/api/categories/${categoryId}`,
  UPDATE_CATEGORY: (categoryId: number) => `${BASE_URL}/api/categories/${categoryId}`,
} as const;

// 카테고리 관련 타입 정의
export interface Category {
  id: number;
  name: string;
  subCategories: Category[];
}

// 카테고리 생성 dto 인터페이스
export interface CreateCategoryRequest {
  name: string;
  parentId?: number; // Long -> number, optional로 설정
}

// 카테고리 수정 dto 인터페이스
export interface UpdateCategoryRequest {
  name: string;
}

// 필터 관련 타입 정의
export interface StudyFilters {
  studies?: string;
  studyDetails?: string;
  techStack?: string;
  progressType?: string;
  recruitmentStatus?: string;
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
  studies: string[];
  studyDetails: string[];
  authorName: string;
  views: number;
  totalComments: number;
  isLiked: boolean;
  totalLikes: number;
  tags?: string[];
}

export interface CreateStudyDTO {
  title: string;
  content: string;
  recruits: number;
  expectedPeriod: string;
  startDate: string;
  endDate: string;
  studies: string[];
  studyDetails: string[];
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
    details: Record<string, string>;
  };
}

// 스터디 API 서비스 객체
export const studyApiService = {
  // 카테고리 계층 구조 조회
  getCategoriesHierarchy: async (): Promise<Category[]> => {
    try {
      const response = await axios.get<Category[]>(STUDY_ENDPOINTS.CATEGORIES_HIERARCHY);
      return response.data;
    } catch (error) {
      console.error('카테고리 데이터를 불러오는데 실패했습니다:', error);
      throw error;
    }
  },

  // 카테고리 생성
  createCategory: async (data: CreateCategoryRequest): Promise<StudyApiResponse<void>> => {
    const response = await axios.post<StudyApiResponse<void>>(STUDY_ENDPOINTS.CREATE_CATEGORY, data);
    return response.data;
  },

  // 카테고리 삭제
  deleteCategory: async (categoryId: number): Promise<StudyApiResponse<void>> => {
    const response = await axios.delete<StudyApiResponse<void>>(STUDY_ENDPOINTS.DELETE_CATEGORY(categoryId));
    return response.data;
  },

  // 카테고리 수정
  updateCategory: async (categoryId: number, data: UpdateCategoryRequest): Promise<StudyApiResponse<void>> => {
    const response = await axios.put<StudyApiResponse<void>>(STUDY_ENDPOINTS.UPDATE_CATEGORY(categoryId), data);
    return response.data;
  },

  // 스터디 목록 조회
  getStudyList: async (): Promise<StudyApiResponse<StudyPost[]>> => {
    try {

      //page = 0, size = 10, filters?: StudyFilters
      // const params = new URLSearchParams({
      //   page: page.toString(),
      //   size: size.toString(),
      //   ...(filters?.studies && { studies: filters.studies }),
      //   ...(filters?.studyDetails && { studyDetails: filters.studyDetails }),
      //   ...(filters?.techStack && { techStack: filters.techStack }),
      //   ...(filters?.progressType && { progressType: filters.progressType }),
      //   ...(filters?.recruitmentStatus && { recruitmentStatus: filters.recruitmentStatus }),
      // });

      const response = await axios.get<StudyApiResponse<StudyPost[]>>(`${STUDY_ENDPOINTS.LIST}`);
      console.log(response);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '스터디 목록을 불러오는데 실패했습니다.');
      }
      throw error;
    }
  },

  // 스터디 상세 조회
  getStudyDetail: async (postId: number): Promise<StudyApiResponse<StudyPost>> => {
    try {
      const response = await axios.get<StudyApiResponse<StudyPost>>(STUDY_ENDPOINTS.DETAIL(postId));
      console.log(response);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '스터디 상세 정보를 불러오는데 실패했습니다.');
      }
      throw error;
    }
  },

  // 스터디 생성
  createPost: async (postData: CreateStudyDTO): Promise<StudyApiResponse<StudyPost>> => {
    try {
      const response = await axios.post<StudyApiResponse<StudyPost>>(STUDY_ENDPOINTS.CREATE, postData, {
        headers: {
          Authorization: `Bearer ${"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxODY4OCIsImF1dGgiOiJST0xFX0FETUlOIiwiaWF0IjoxNzQ0MTI2MTYxLCJleHAiOjE3NDQxMjcxNjF9.vtnFcA6hM8aw5qS2kj1OhuHLjyacee43H_mwlpwI6Zs"}`,
        },
        withCredentials: true,
      });
      console.log('게시글 생성완료');
      console.log(response);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.log(postData);
        throw new Error(error.response?.data?.message || '스터디 생성에 실패했습니다.');
      }
      throw error;
    }
  },

  // 스터디 수정
  updatePost: async (postId: number, postData: UpdateStudyDTO): Promise<StudyApiResponse<StudyPost>> => {
    try {
      const response = await axios.post<StudyApiResponse<StudyPost>>(STUDY_ENDPOINTS.UPDATE(postId), postData);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '스터디 수정에 실패했습니다.');
      }
      throw error;
    }
  },

  // 스터디 삭제
  deletePost: async (postId: number): Promise<StudyApiResponse<void>> => {
    try {
      const response = await axios.delete<StudyApiResponse<void>>(STUDY_ENDPOINTS.DELETE(postId));
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '스터디 삭제에 실패했습니다.');
      }
      throw error;
    }
  },

  // 좋아요 추가
  addLike: async (postId: number): Promise<StudyApiResponse<void>> => {
    try {
      const response = await axios.post<StudyApiResponse<void>>(STUDY_ENDPOINTS.LIKE(postId));
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '좋아요 추가에 실패했습니다.');
      }
      throw error;
    }
  },

  // 좋아요 취소
  removeLike: async (postId: number): Promise<StudyApiResponse<void>> => {
    try {
      const response = await axios.delete<StudyApiResponse<void>>(STUDY_ENDPOINTS.UNLIKE(postId));
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '좋아요 취소에 실패했습니다.');
      }
      throw error;
    }
  },
};

export default studyApiService;