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

const getRoadmapFormData = async () => {
  const { data } = await auth.get('/api/categories/roadmap-form-data');
  console.log('hi');
  return data;
};

const postWorksheetsFormData = async (id: number) => {
  const { data } = await auth.post(`/api/${id}/worksheets`);
  console.log(data);
  return data;
};
const postRoadmapFormData = async (createFormData: CreateFormDataType) => {
  const { data } = await auth.post('/api/roadmap/generate', createFormData);
  console.log(data);
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
      console.log(data);

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

export const usePostWorksheetsCreate = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: postWorksheetsFormData,
    onMutate: () => {
      console.log('워크시트 생성 시도');
      queryClient.removeQueries({ queryKey: ['worksheetStatus'] });
    },
    onSuccess: (data) => {
      console.log('워크시트 생성 성공');
      console.log(data);

      queryClient.setQueryData(['worksheetStatus'], {
        status: 'success',
        data,
      });
    },
    onError: () => {
      console.log('워크시트 생성 실패');
      toast('워크시트 생성에 실패하엿습니다.', {
        description: '',
      });
    },
  });
};
