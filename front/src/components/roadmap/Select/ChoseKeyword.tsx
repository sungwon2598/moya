import { Question } from "@/features/roadmap/hooks/useRoadmapQuestions";
import { AnswerItem } from "@/pages/roadmap/CreatePage";
import { RoadmapQuestionStageType } from "@/pages/roadmap/type";
import { type Dispatch, type SetStateAction } from "react";
import { Button } from "@/components/shared/ui/button";
import {
  CreateFormDataType,
  usePostRoadmapCreate,
} from "@/features/roadmap/api/createRoadmap";
const selectBTN =
  "inline-block px-4 py-2 mr-2 bg-blue-100 rounded min-w-32 min-h-10 hover:bg-blue-200 transition-colors";
const pBTN = "mt-2";
interface ChoseKeywordProp {
  AllQuestionsCompleted: boolean;
  answers: AnswerItem[] | null;
  setAnswers: Dispatch<SetStateAction<AnswerItem[] | null>>;
  questions: Question[];
  setRoadmapQuestionStage: Dispatch<SetStateAction<RoadmapQuestionStageType>>;
}
export default function ChoseKeyword({
  AllQuestionsCompleted,
  answers,
  setAnswers,
  questions,
  setRoadmapQuestionStage,
}: ChoseKeywordProp) {
  const { mutate, isPending, isError, isSuccess, data } =
    usePostRoadmapCreate();

  const navigateToQuestion = (questionName: string) => {
    const question = questions.find((q) => q.name === questionName);
    if (!question) return;
    const allPreviousQuestionsAnswered = Array.from(
      { length: question.id - 1 },
      (_, i) => i + 1
    ).every((id) => answers?.some((answer) => answer.questionNumber === id));
    if (allPreviousQuestionsAnswered) {
      setRoadmapQuestionStage({ currentStatusNumber: question.id });
    } else {
      alert("이전 질문에 대답해주세요.");
    }
  };
  const findSelectedAnswer = (questionName: string) => {
    const question = questions.find((q) => q.name === questionName);
    if (!question) return null;

    const answer = answers?.find((a) => a.questionNumber === question.id);
    if (!answer) return null;

    const choice = question.choices.find(
      (c) => c.id.toString() === answer.choiceId.toString()
    );
    return choice?.name || null;
  };
  const learningObjective = findSelectedAnswer("learningObjective");
  const learningDuration = findSelectedAnswer("duration");
  const mainCategory = findSelectedAnswer("mainCategory");
  const subCategory = findSelectedAnswer("subCategory");
  const handlerReset = () => {
    navigateToQuestion("learningObjective");
    setAnswers(null);
  };

  const handleRoadmapCreateSubmit = () => {
    const formData: CreateFormDataType = {
      mainCategory: String(
        answers?.find((answer) => answer.name === "mainCategory")
          ?.choiceValue ?? ""
      ),
      subCategory: String(
        answers?.find((answer) => answer.name === "subCategory")?.choiceValue ??
          ""
      ),
      currentLevel: "1",
      duration: Number(
        answers?.find((answer) => answer.name === "duration")?.choiceValue ?? ""
      ),
      learningObjective: String(
        answers?.find((answer) => answer.name === "learningObjective")
          ?.choiceId ?? ""
      ),
    };
    console.log(formData);
    mutate(formData);
  };
  return (
    <article
      className={`${AllQuestionsCompleted ? "col-span-2 text-center" : ""} p-8 mb-12 text-3xl font-bold bg-neutral-100 rounded-2xl`}
    >
      <div
        className={`${AllQuestionsCompleted ? "flex gap-2 items-center justify-center" : ""}`}
      >
        <p>나는</p>
        <p className={`${pBTN}`}>
          <button
            className={`${selectBTN}`}
            onClick={() => navigateToQuestion("learningObjective")}
          >
            {learningObjective}
          </button>
          을 위해
        </p>
      </div>
      <div
        className={`${AllQuestionsCompleted ? "flex gap-2 items-center justify-center" : ""} mt-8`}
      >
        <p>
          <button
            className={`${selectBTN}`}
            onClick={() => navigateToQuestion("duration")}
          >
            {learningDuration}
          </button>
          동안
        </p>
        <p className={`${pBTN}`}>
          <button
            className={`${selectBTN}`}
            onClick={() => navigateToQuestion("mainCategory")}
          >
            {mainCategory}
          </button>
          의
        </p>
      </div>
      <div
        className={`${AllQuestionsCompleted ? "flex gap-2 items-center justify-center" : ""} `}
      >
        <p className={`${pBTN}`}>
          <button
            className={`${selectBTN}`}
            onClick={() => navigateToQuestion("subCategory")}
          >
            {subCategory}
          </button>
          을/를
        </p>
        <p className={`${pBTN}`}>공부하고 싶어요.</p>
      </div>
      {AllQuestionsCompleted && (
        <div>
          <Button variant="secondary" onClick={() => handlerReset()}>
            다시 선택하기
          </Button>
          <Button onClick={handleRoadmapCreateSubmit}>로드맵 생성하기</Button>
        </div>
      )}
    </article>
  );
}
