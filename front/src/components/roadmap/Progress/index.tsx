import { Progress as ShadcnProgress } from "@/components/shared/ui/progress";
import { RoadmapQuestionStageType } from "@/pages/roadmap/type";

interface ProgressProp {
  roadmapQuestionStage: RoadmapQuestionStageType;
}

export default function Progress({ roadmapQuestionStage }: ProgressProp) {
  const progressPercentage =
    (roadmapQuestionStage.currentStatusNumber /
      roadmapQuestionStage.lastStatusNumber) *
    100;

  return (
    <section className="col-span-2 my-6">
      <ShadcnProgress value={progressPercentage} className="h-2" />
    </section>
  );
}
