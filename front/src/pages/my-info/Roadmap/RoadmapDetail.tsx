import { useRoadmapDetail } from '@/features/myinfo/api/getRoadmap';
import { useParams } from 'react-router-dom';

export default function RoadmapDetail() {
  const { roadmapId } = useParams();
  const { data: roadmapDetailData, isLoading, isError } = useRoadmapDetail(Number(roadmapId));
  console.log(roadmapDetailData);
  if (isLoading) return <div>불러오는 중...</div>;
  if (isError) return <div>에러가 발생했어요!</div>;
  return <div>RoadmapDetail</div>;
}
