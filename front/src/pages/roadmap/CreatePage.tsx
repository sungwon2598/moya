import { useState } from "react";
import Title from "../../components/roadmap/Title";
import Progress from "../../components/roadmap/Progress";
import Select from "../../components/roadmap/Select";
import { RoadmapQuestionStageType } from "./type";
import { roadmapCreationQuestion } from "./data/createRoadmap";
export default function CreatePage() {
  const [roadmapQuestionStage, setRoadmapQuestionStage] =
    useState<RoadmapQuestionStageType>({
      currentStatusNumber: 1,
      lastStatusNumber: roadmapCreationQuestion.length,
      answer: [],
    });

  return (
    <div className="@container px-4 mx-auto grid grid-cols-2 gap-4">
      <Title />
      <Progress roadmapQuestionStage={roadmapQuestionStage} />
      <Select
        roadmapQuestionStage={roadmapQuestionStage}
        setRoadmapQuestionStage={setRoadmapQuestionStage}
      />
    </div>
  );
}
