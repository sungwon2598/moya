import { auth, BASE_URL } from '../config';
import axios from 'axios';
import type {
  Category,
  StudyApiResponse,
  StudyPost,
  CreateCategoryRequest,
  UpdateCategoryRequest,
  CreateStudyDTO,
  UpdateStudyDTO,
  HotPost,
  CreateCommentDTO,
} from '@/types/study';

export const STUDY_ENDPOINTS = {
  LIST: (page: number) => `${BASE_URL}/api/posts?page=${page}`,
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
  HOTSTUDYLIST: `${BASE_URL}/api/posts/popular`,
  COMMENTS: (postId: number) => `${BASE_URL}/api/posts/${postId}/comments`,
  REPLY_COMMENTS: (postId: number, commentId: number) => `${BASE_URL}/api/posts/${postId}/comments/${commentId}`,
  DELETE_COMMENTS: (postId: number, commentId: number) => `${BASE_URL}/api/posts/${postId}/comments/${commentId}`,
} as const;

export const studyService = {
  // 카테고리
  getCategoriesHierarchy: async (): Promise<Category[]> => {
    try {
      const response = await auth.get<Category[]>(STUDY_ENDPOINTS.CATEGORIES_HIERARCHY);
      return response.data;
    } catch (error) {
      console.error('카테고리 데이터를 불러오는데 실패했습니다:', error);
      throw error;
    }
  },

  createCategory: async (data: CreateCategoryRequest): Promise<StudyApiResponse<void>> => {
    const response = await auth.post<StudyApiResponse<void>>(STUDY_ENDPOINTS.CREATE_CATEGORY, data);
    return response.data;
  },

  deleteCategory: async (categoryId: number): Promise<StudyApiResponse<void>> => {
    const response = await auth.delete<StudyApiResponse<void>>(STUDY_ENDPOINTS.DELETE_CATEGORY(categoryId));
    return response.data;
  },

  updateCategory: async (categoryId: number, data: UpdateCategoryRequest): Promise<StudyApiResponse<void>> => {
    const response = await auth.put<StudyApiResponse<void>>(STUDY_ENDPOINTS.UPDATE_CATEGORY(categoryId), data);
    return response.data;
  },

  // 스터디
  getStudyList: async (page: number): Promise<StudyApiResponse<StudyPost[]>> => {
    try {
      const response = await auth.get<StudyApiResponse<StudyPost[]>>(STUDY_ENDPOINTS.LIST(page));
      console.log(response.data);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '스터디 목록을 불러오는데 실패했습니다.');
      }
      throw error;
    }
  },

  getStudyDetail: async (postId: number): Promise<StudyApiResponse<StudyPost>> => {
    try {
      const response = await auth.get<StudyApiResponse<StudyPost>>(STUDY_ENDPOINTS.DETAIL(postId));
      //   console.log(response);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '스터디 상세 정보를 불러오는데 실패했습니다.');
      }
      throw error;
    }
  },

  createPost: async (postData: CreateStudyDTO): Promise<StudyApiResponse<StudyPost>> => {
    try {
      const response = await auth.post<StudyApiResponse<StudyPost>>(STUDY_ENDPOINTS.CREATE, postData);
      //   console.log('게시글 생성완료');
      //   console.log(response);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.log(postData);
        throw new Error(error.response?.data?.message || '스터디 생성에 실패했습니다.');
      }
      throw error;
    }
  },

  updatePost: async (postId: number, postData: UpdateStudyDTO): Promise<StudyApiResponse<StudyPost>> => {
    try {
      const response = await auth.post<StudyApiResponse<StudyPost>>(STUDY_ENDPOINTS.UPDATE(postId), postData);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '스터디 수정에 실패했습니다.');
      }
      throw error;
    }
  },

  deletePost: async (postId: number): Promise<StudyApiResponse<void>> => {
    try {
      const response = await auth.delete<StudyApiResponse<void>>(STUDY_ENDPOINTS.DELETE(postId));
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '스터디 삭제에 실패했습니다.');
      }
      throw error;
    }
  },

  addLike: async (postId: number): Promise<StudyApiResponse<void>> => {
    try {
      const response = await auth.post<StudyApiResponse<void>>(STUDY_ENDPOINTS.LIKE(postId), {});
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.log(error.response);
        throw new Error(error.response?.data?.message || '좋아요 추가에 실패했습니다.');
      }
      throw error;
    }
  },

  removeLike: async (postId: number): Promise<StudyApiResponse<void>> => {
    try {
      const response = await auth.delete<StudyApiResponse<void>>(STUDY_ENDPOINTS.UNLIKE(postId));
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '좋아요 취소에 실패했습니다.');
      }
      throw error;
    }
  },

  getHotStudyList: async (): Promise<StudyApiResponse<HotPost[]>> => {
    try {
      const response = await auth.get<StudyApiResponse<HotPost[]>>(STUDY_ENDPOINTS.HOTSTUDYLIST);
      console.log(response);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '핫 스터디 조회 실패.');
      }
      throw error;
    }
  },

  createComment: async (postId: number, commentData: CreateCommentDTO): Promise<StudyApiResponse<Comment>> => {
    try {
      const response = await auth.post<StudyApiResponse<Comment>>(STUDY_ENDPOINTS.COMMENTS(postId), commentData);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.error('댓글/대댓글 생성 실패:', error.response?.data);
        throw new Error(error.response?.data?.message || '댓글 생성에 실패했습니다.');
      }
      throw error;
    }
  },

  createComments: async (postId: number, commentData: CreateCommentDTO): Promise<StudyApiResponse<Comment>> => {
    return studyService.createComment(postId, commentData);
  },

  createReplyComments: async (
    postId: number,
    parentCommentId: number,
    commentData: CreateCommentDTO
  ): Promise<StudyApiResponse<Comment>> => {
    const replyData = {
      ...commentData,
      parentCommentId: parentCommentId,
    };
    return studyService.createComment(postId, replyData);
  },

  deleteComments: async (postId: number, commentId: number): Promise<StudyApiResponse<void>> => {
    try {
      const response = await auth.delete<StudyApiResponse<void>>(STUDY_ENDPOINTS.DELETE_COMMENTS(postId, commentId));
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '댓글 삭제에 실패했습니다.');
      }
      throw error;
    }
  },
};
