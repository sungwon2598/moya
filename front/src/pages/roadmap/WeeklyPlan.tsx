import { Button } from '@/components';
import { useRotatingMessage } from '@/features/roadmap/hooks/useRotatingMessage';
import { useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

type Day = {
  day: number;
  dailyKeyword: string;
};

type Week = {
  week: number;
  weeklyKeyword: string;
  dailyPlans: Day[];
};
type RoadmapData = {
  curriculumEvaluation: string;
  overallTips: string[];
  weeklyPlans: Week[];
};

type RoadmapStatus = {
  status: string;
  data: RoadmapData;
};
export default function WeeklyPlan() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { data } = queryClient.getQueryData<RoadmapStatus>(['roadmapStatus']) ?? {};
  const { currentMessage } = useRotatingMessage(data && data?.overallTips?.length > 0 ? data.overallTips : [''], 5000);

  return (
    <div className="@container px-4">
      <h4 className="py-2">{data?.curriculumEvaluation}</h4>

      {data?.overallTips && data.overallTips.length > 0 && (
        <div className="relative pb-2 group">
          <div className="p-2 rounded bg-neutral-100">
            <p className="mb-1 font-semibold">📚 Tip.</p>
            <p>{currentMessage as string}</p>
          </div>
          <div className="absolute z-10 hidden w-full p-2 border rounded shadow-2xl border-neutral-400 bg-neutral-200 group-hover:block">
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

      <div className="grid grid-cols-1 gap-4 mb-12 overflow-hidden border-neutral-200 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {data?.weeklyPlans?.map((week: Week) => (
          <div key={week.week} className="pt-4 border border-neutral-200">
            <div className="px-3 border-b">
              <h6 className="text-moya-primary">{week.week}주차</h6>
              <h5 className="py-2">{week.weeklyKeyword}</h5>
            </div>

            <div className="">
              {week.dailyPlans.map((day: Day, index: number) => (
                <p key={day.day} className={`flex ${index < week.dailyPlans.length - 1 ? 'border-b' : 'border-0'}`}>
                  <span className="inline-block py-1 mr-2 text-center border-r basis-1/5 text-neutral-700">
                    {day.day}일차
                  </span>
                  <span className="py-1 basis-4/5"> {day.dailyKeyword}</span>
                </p>
              ))}
            </div>
          </div>
        ))}
      </div>
      <div className="my-4 text-center">
        <Button onClick={() => navigate('/my-info/roadmap')}>생성 완료</Button>
      </div>
    </div>
  );
}
