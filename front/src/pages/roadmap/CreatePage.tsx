import { useState } from 'react';
import Title from '../../components/features/roadmap/Title';
import Progress from '../../components/features/roadmap/Progress';
import Select from '../../components/features/roadmap/Select';
import { RoadmapQuestionStageType } from './type';
import { roadmapCreationQuestion } from './data/createRoadmap';
export default function CreatePage() {
  const [roadmapQuestionStage, setRoadmapQuestionStage] = useState<RoadmapQuestionStageType>({
    currentStatusNumber: 1,
    lastStatusNumber: roadmapCreationQuestion.length,
    answer: [],
  });

  return (
    <div className="@container mx-auto grid grid-cols-2 gap-4 px-4">
      <Title />
      <Progress roadmapQuestionStage={roadmapQuestionStage} />
      <Select roadmapQuestionStage={roadmapQuestionStage} setRoadmapQuestionStage={setRoadmapQuestionStage} />
    </div>
  );
}
