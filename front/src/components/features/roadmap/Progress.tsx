import { Progress as ShadcnProgress } from '@/components/shared/ui/progress';
// import { RoadmapQuestionStageType } from '@/pages/roadmap/type';

export interface RoadmapQuestionStageType {
  currentStatusNumber: number;
}

interface ProgressProp {
  roadmapQuestionStage: RoadmapQuestionStageType;
  TotalQuestionsNumber: number;
}

export default function Progress({ roadmapQuestionStage, TotalQuestionsNumber }: ProgressProp) {
  const progressPercentage = (roadmapQuestionStage.currentStatusNumber / TotalQuestionsNumber) * 100;

  return (
    <section className="col-span-2 my-6">
      <ShadcnProgress value={progressPercentage} className="h-2" />
    </section>
  );
}
