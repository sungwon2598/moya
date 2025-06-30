import { Button } from '@/components';
import RotatingMessage from '@/components/features/roadmap/RotatingMessage';
import WeeklyPlanCard from '@/components/features/roadmap/WeeklyPlan/WeeklyPlanCard';
import { useNavigate } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';

export default function WeeklyPlan() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // 캐시된 roadmapStatus 데이터 직접 가져오기
  const roadmapStatus = queryClient.getQueryData(['roadmapStatus']);
  const roadmapData = roadmapStatus?.data;

  console.log('roadmapStatus', roadmapStatus);
  console.log('roadmapData', roadmapData);

  return (
    <div className="@container overflow-hidden px-4">
      <h4 className="py-2">{roadmapData?.curriculumEvaluation}</h4>
      {roadmapData?.overallTips && roadmapData.overallTips.length > 0 && <RotatingMessage data={roadmapData} />}
      <div className="mb-12 border-neutral-200">
        <WeeklyPlanCard weeks={roadmapData?.weeklyPlans} />
      </div>
      <div className="my-4 text-center">
        <Button onClick={() => navigate('/my-info/roadmap')}>생성 완료</Button>
      </div>
    </div>
  );
}
