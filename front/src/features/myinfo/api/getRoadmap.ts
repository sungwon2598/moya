import { axiosInstance } from '@/core/config/apiConfig';
import { useQuery } from '@tanstack/react-query';

const getMyRoadmapListData = async () => {
  const { data } = await axiosInstance.get('/api/roadmap/myroadmaps');
  console.log(data);
  return data;
};
export const useMyRoadmapList = () => {
  return useQuery({
    queryKey: ['myRoadmapForm'],
    queryFn: getMyRoadmapListData,
  });
};
