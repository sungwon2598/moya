import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { auth } from '@/services/config';

export interface CreateFormDataType {
  mainCategory: string;
  subCategory: string;
  currentLevel: string;
  duration: number;
  learningObjective: string;
}

// 응답 타입들 정의
export interface RoadmapFormData {
  mainCategories: string[];
  subCategories: Record<string, string[]>;
  levels: string[];
}

export interface RoadmapResponse {
  id: number;
  title: string;
  // 필요에 따라 다른 속성들 추가
}

export interface WorksheetResponse {
  id: number;
  worksheets: any[];
}

// API 함수들 - 반환 타입 명시
const getRoadmapFormData = async (): Promise<RoadmapFormData> => {
  try {
    const { data } = await auth.get('/api/categories/roadmap-form-data');
    return data;
  } catch (error) {
    console.error('로드맵 폼 데이터 조회 실패:', error);
    throw error;
  }
};

const postWorksheetsFormData = async (
  id: number,
  mainCategory: string,
  subCategory: string
): Promise<WorksheetResponse> => {
  try {
    const { data } = await auth.post(`/api/roadmap/${id}/worksheets`, {
      mainCategory,
      subCategory,
    });
    console.log('워크시트 생성 응답:', data);
    return data;
  } catch (error) {
    console.error('워크시트 생성 실패:', error);
    throw error;
  }
};

const postRoadmapFormData = async (createFormData: CreateFormDataType): Promise<RoadmapResponse> => {
  try {
    const { data } = await auth.post('/api/roadmap/generate', createFormData);
    console.log('로드맵 생성 응답:', data);
    return data;
  } catch (error) {
    console.error('로드맵 생성 실패:', error);
    throw error;
  }
};

export const useRoadmapFormData = () => {
  return useQuery({
    queryKey: ['roadmapForm'],
    queryFn: getRoadmapFormData,
    staleTime: 1000 * 60 * 5, // 5분
    retry: 3, // 실패 시 3번까지 재시도
    retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000), // 지수 백오프
  });
};

export const usePostRoadmapCreate = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: postRoadmapFormData,
    onMutate: () => {
      console.log('로드맵 생성 시도');

      queryClient.removeQueries({ queryKey: ['roadmapStatus'] });

      queryClient.setQueryData(['roadmapStatus'], {
        status: 'loading',
        data: null,
      });
    },
    onSuccess: (data) => {
      console.log('로드맵 생성 성공:', data);

      // 성공 상태 설정
      queryClient.setQueryData(['roadmapStatus'], {
        status: 'success',
        data,
      });

      // 관련 쿼리들 무효화하여 최신 데이터 보장
      queryClient.invalidateQueries({ queryKey: ['roadmaps'] });

      // 성공 토스트
      toast.success('로드맵이 성공적으로 생성되었습니다!', {
        description: '주간 계획 페이지로 이동합니다.',
        duration: 3000,
      });

      navigate('/roadmap/weeklyPlan');
    },
    onError: (error) => {
      console.error('로드맵 생성 실패:', error);

      queryClient.setQueryData(['roadmapStatus'], {
        status: 'error',
        error: error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.',
      });

      toast.error('로드맵 생성에 실패했습니다.', {
        description: '잠시 후 다시 시도해 주세요.',
        duration: 5000,
      });
    },
  });
};

export const usePostWorksheetsCreate = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, mainCategory, subCategory }: { id: number; mainCategory: string; subCategory: string }) =>
      postWorksheetsFormData(id, mainCategory, subCategory),
    onMutate: () => {
      console.log('워크시트 생성 시도');

      queryClient.removeQueries({ queryKey: ['worksheetStatus'] });

      queryClient.setQueryData(['worksheetStatus'], {
        status: 'loading',
        data: null,
      });
    },
    onSuccess: (data) => {
      console.log('워크시트 생성 성공:', data);

      queryClient.setQueryData(['worksheetStatus'], {
        status: 'success',
        data,
      });

      queryClient.invalidateQueries({ queryKey: ['worksheets'] });

      toast.success('워크시트가 성공적으로 생성되었습니다!', {
        duration: 3000,
      });
    },
    onError: (error) => {
      console.error('워크시트 생성 실패:', error);

      queryClient.setQueryData(['worksheetStatus'], {
        status: 'error',
        error: error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.',
      });

      toast.error('워크시트 생성에 실패했습니다.', {
        description: '잠시 후 다시 시도해 주세요.',
        duration: 5000,
      });
    },
  });
};
