import { auth } from '@/services/config';
import { useQuery } from '@tanstack/react-query';

const getMyRoadmapListData = async () => {
  const { data } = await auth.get('/api/roadmap/myroadmaps');
  return data;
};
const getMyRoadmapDetailData = async (id: number) => {
  const { data } = await auth.get(`/api/roadmap/myroadmaps/${id}`);
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
