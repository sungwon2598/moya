import { Button } from '@/components';
import { useRotatingMessage } from '@/features/roadmap/hooks/useRotatingMessage';
import { useQueryClient } from '@tanstack/react-query';
import { useEffect } from 'react';
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
  useEffect(() => {
    console.log(data);
  });
  return (
    <div className="@container px-4">
      <h4 className="py-2">{data?.curriculumEvaluation}</h4>

      {data?.overallTips && data.overallTips.length > 0 && (
        <div className="group relative pb-2">
          <div className="rounded bg-neutral-100 p-2">
            <p className="mb-1 font-semibold">📚 Tip.</p>
            <p>{currentMessage as string}</p>
          </div>
          <div className="absolute z-10 hidden w-full rounded border border-neutral-400 bg-neutral-200 p-2 shadow-2xl group-hover:block">
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

      <div className="mb-12 grid grid-cols-1 gap-4 overflow-hidden border-neutral-200 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {data?.weeklyPlans?.map((week: Week) => (
          <div key={week.week} className="border border-neutral-200 pt-4">
            <div className="border-b px-3">
              <h6 className="text-moya-primary">{week.week}주차</h6>
              <h5 className="py-2">{week.weeklyKeyword}</h5>
            </div>

            <div className="">
              {week.dailyPlans.map((day: Day, index: number) => (
                <p key={day.day} className={`flex ${index < week.dailyPlans.length - 1 ? 'border-b' : 'border-0'}`}>
                  <span className="mr-2 inline-block basis-1/5 border-r py-1 text-center text-neutral-700">
                    {day.day}일차
                  </span>
                  <span className="basis-4/5 py-1"> {day.dailyKeyword}</span>
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
