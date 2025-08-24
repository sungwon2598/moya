import { auth, BASE_URL } from '../config';
import axios from 'axios';
import type { RoadmapRequest, WeeklyRoadmapResponse, RoadMapSimpleDto } from '@/types/roadmap';

export const ROADMAP_ENDPOINTS = {
  GENERATE: `${BASE_URL}/api/roadmap/generate`,
  GENERATE_WORKSHEETS: (roadmapId: number) => `${BASE_URL}/api/roadmap/${roadmapId}/worksheets`,
  GET_CATEGORY_ROADMAPS: (categoryId: number) => `${BASE_URL}/api/roadmap/categories/${categoryId}/roadmaps`,
} as const;

export const roadmapService = {
  generateWeeklyRoadmap: async (request: RoadmapRequest): Promise<WeeklyRoadmapResponse> => {
    try {
      const response = await auth.post<WeeklyRoadmapResponse>(ROADMAP_ENDPOINTS.GENERATE, request);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '로드맵 생성에 실패했습니다.');
      }
      throw error;
    }
  },
  generateWorksheets: async (roadmapId: number): Promise<void> => {
    try {
      await auth.post(ROADMAP_ENDPOINTS.GENERATE_WORKSHEETS(roadmapId));
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
      const response = await auth.get<RoadMapSimpleDto[]>(ROADMAP_ENDPOINTS.GET_CATEGORY_ROADMAPS(categoryId));
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '카테고리별 로드맵 목록을 불러오는데 실패했습니다.');
      }
      throw error;
    }
  },
};
