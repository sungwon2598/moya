import { axiosInstance } from '@/api/authApi';
import { useQuery } from '@tanstack/react-query';

const getMyRoadmapListData = async () => {
  const { data } = await axiosInstance.get('/api/roadmap/myroadmaps');
  return data;
};
const getMyRoadmapDetailData = async (id: number) => {
  const { data } = await axiosInstance.get(`/api/roadmap/roadmaps/${id}`);
  return data;
};
export const useMyRoadmapList = () => {
  return useQuery({
    queryKey: ['myRoadmapList'],
    queryFn: getMyRoadmapListData,
  });
};
export const useRoadmapDetail = (id: number) => {
  return useQuery({
    queryKey: ['RoadmapDetail', id],
    queryFn: () => getMyRoadmapDetailData(id),
    enabled: !!id,
  });
};
