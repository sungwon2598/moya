import { useEffect, useState } from "react";
import Title from "../../components/roadmap/Title.tsx";
import Select from "../../components/roadmap/Select/Select";
import { RoadmapQuestionStageType } from "./type";
import { useRoadmapQuestions } from "@/features/roadmap/hooks/useRoadmapQuestions";
import Progress from "@/components/roadmap/Progress";

export type AnswerItem = {
  questionNumber: number | string;
  name?: string;
  choiceId: string;
  choiceValue: string | number;
};

export default function CreatePage() {
  //api요청
  const { questions, isLoading, error, setSelectedCategoryId } =
    useRoadmapQuestions();
  // 질문ID, 선택ID 저장
  const [answers, setAnswers] = useState<AnswerItem[] | null>([]);
  // 1번 질문부터, 현재 질문페이지 저장
  const [roadmapQuestionStage, setRoadmapQuestionStage] =
    useState<RoadmapQuestionStageType>({
      currentStatusNumber: 1,
    });

  const categoryQuestion = questions.find((q) => q.name === "mainCategory");

  useEffect(() => {
    if (categoryQuestion) {
      const categoryAnswer = answers?.find(
        (answer) => answer.questionNumber === categoryQuestion.id
      );
      if (categoryAnswer) {
        setSelectedCategoryId(categoryAnswer.choiceId);
      }
    }
  }, [answers, categoryQuestion, setSelectedCategoryId]);

  if (isLoading) return <div>로딩중...</div>;
  if (error) return <div>{error.message}</div>;

  return (
    <div className="@container mx-auto grid grid-cols-2 gap-4 px-4">
      <Title />
      <Progress
        TotalQuestionsNumber={questions.length}
        roadmapQuestionStage={roadmapQuestionStage}
      />
      <Select
        questions={questions}
        answers={answers}
        setAnswers={setAnswers}
        roadmapQuestionStage={roadmapQuestionStage}
        setRoadmapQuestionStage={setRoadmapQuestionStage}
      />
    </div>
  );
}
