import { Day } from '@/types/roadmap.types';

interface DayStudyPlanType {
  dailyPlans: Day;
}
export default function DayStudyPlan({ dailyPlans }: DayStudyPlanType) {
  return (
    <div>
      {dailyPlans.day}
      {dailyPlans.dailyKeyword}
    </div>
  );
}
