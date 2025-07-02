import { Day } from '@/types/roadmap.types';

interface DayStudyPlanType {
  dailyPlans: Day;
}
export default function DayStudyPlan({ dailyPlans }: DayStudyPlanType) {
  console.log(dailyPlans);
  return (
    <div className="text-black">
      {dailyPlans.day}
      {dailyPlans.dailyKeyword}
      {dailyPlans.worksheet}
    </div>
  );
}
