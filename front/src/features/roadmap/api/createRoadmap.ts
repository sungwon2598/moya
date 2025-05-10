import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { axiosInstance } from '@core/config/apiConfig';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export interface CreateFormDataType {
  mainCategory: string;
  subCategory: string;
  currentLevel: string;
  duration: number;
  learningObjective: string;
}

const getRoadmapFormData = async () => {
  const { data } = await axiosInstance.get('/api/categories/roadmap-form-data');
  return data;
};

const postRoadmapFormData = async (createFormData: CreateFormDataType) => {
  const { data } = await axiosInstance.post('/api/roadmap/generate', createFormData);
  return data;
};

export const useRoadmapFormData = () => {
  return useQuery({
    queryKey: ['roadmapForm'],
    queryFn: getRoadmapFormData,
    staleTime: 1000 * 60 * 5,
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
    },
    onSuccess: (data) => {
      console.log('로드맵 생성 성공');
      queryClient.setQueryData(['roadmapStatus'], {
        status: 'success',
        data,
      });
      navigate('/roadmap/weeklyPlan');
    },
    onError: () => {
      console.log('로드맵 생성 실패');
      toast('로드맵 생성에 실패하엿습니다.', {
        description: '',
      });
      navigate('/roadmap/create');
    },
  });
};
