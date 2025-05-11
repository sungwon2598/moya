import { Button } from '@/components';
import WeeklyPlanCard from '@/components/features/roadmap/WeeklyPlan/WeeklyPlanCard';
import { usePostRoadmapCreate } from '@/features/roadmap/api/createRoadmap';
import { useRotatingMessage } from '@/features/roadmap/hooks/useRotatingMessage';
import { useNavigate } from 'react-router-dom';

export default function WeeklyPlan() {
  const navigate = useNavigate();
  const { data } = usePostRoadmapCreate();
  const { currentMessage } = useRotatingMessage(data && data?.overallTips?.length > 0 ? data.overallTips : [''], 5000);
  return (
    <div className="@container overflow-hidden px-4">
      <h4 className="py-2">{data?.curriculumEvaluation}</h4>
      {data?.overallTips && data.overallTips.length > 0 && (
        <div className="group relative pb-2">
          <div className="rounded bg-neutral-100 p-2">
            <p className="mb-1 font-semibold">📚 Tip.</p>
            <p>{currentMessage as string}</p>
          </div>
          <div className="absolute right-1/2 top-1/2 z-10 hidden w-full -translate-x-1/2 rounded border border-neutral-400 bg-neutral-200 p-2 shadow-2xl group-hover:block">
            {data.overallTips.map((tip: string, index: number) => (
              <p
                key={index}
                className={`${currentMessage === tip ? 'text-moya-primary font-semibold' : 'text-neutral-400'}`}>
                {tip}
              </p>
            ))}
          </div>
        </div>
      )}

      <div className="mb-12 border-neutral-200">
        <WeeklyPlanCard weeks={data?.weeklyPlans} />
      </div>
      <div className="my-4 text-center">
        <Button onClick={() => navigate('/my-info/roadmap')}>생성 완료</Button>
      </div>
    </div>
  );
}
