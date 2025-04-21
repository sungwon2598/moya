import { Button } from "@/components/shared/ui/button";
import { Question } from "@/features/roadmap/hooks/useRoadmapQuestions";
import { AnswerItem } from "@/pages/roadmap/CreatePage";
import { RoadmapQuestionStageType } from "@/pages/roadmap/type";
import { useState } from "react";
import type { Dispatch, SetStateAction } from "react";
import ChoseKeyword from "./ChoseKeyword";

interface SelectProp {
  answers: AnswerItem[] | null;
  setAnswers: Dispatch<SetStateAction<AnswerItem[] | null>>;
  questions: Question[];
  roadmapQuestionStage: RoadmapQuestionStageType;
  setRoadmapQuestionStage: Dispatch<SetStateAction<RoadmapQuestionStageType>>;
}
interface SelectedValue {
  id: string;
  name: string | number;
}
export default function Select({
  answers,
  setAnswers,
  questions,
  roadmapQuestionStage,
  setRoadmapQuestionStage,
}: SelectProp) {
  const { currentStatusNumber } = roadmapQuestionStage;
  const [selectedValue, setSelectedValue] = useState<SelectedValue | null>();
  const handleChoiceSubmit = (choiceId: SelectedValue) => {
    // 현재 질문 정보 가져오기
    const currentQuestion = questions[currentStatusNumber - 1];

    setAnswers((prev) => {
      const newAnswers = [...(prev ?? [])];

      const existingAnswerIndex = newAnswers.findIndex(
        (answer) => answer.questionNumber === currentStatusNumber
      );

      if (existingAnswerIndex !== -1) {
        newAnswers[existingAnswerIndex] = {
          ...newAnswers[existingAnswerIndex],
          choiceId: choiceId.id,
          name: currentQuestion.name,
          choiceValue: choiceId.name,
        };
      } else {
        newAnswers.push({
          questionNumber: currentStatusNumber,
          choiceId: choiceId.id,
          name: currentQuestion.name,
          choiceValue: choiceId.name,
        });
      }

      return newAnswers;
    });

    setSelectedValue(null);
    setRoadmapQuestionStage((prevStage) => ({
      ...prevStage,
      currentStatusNumber: prevStage.currentStatusNumber + 1,
    }));
  };
  const disableBtn = () => {
    return answers?.some(
      (item) => item.questionNumber === currentStatusNumber
    ) === true
      ? false
      : selectedValue === null;
  };
  const AllQuestionsCompleted = answers?.length === questions.length;
  return (
    <>
      <ChoseKeyword
        answers={answers}
        setAnswers={setAnswers}
        questions={questions}
        setRoadmapQuestionStage={setRoadmapQuestionStage}
        AllQuestionsCompleted={AllQuestionsCompleted}
      />
      {!AllQuestionsCompleted && (
        <section>
          <h3>{questions[currentStatusNumber - 1]?.title}</h3>
          <p>{questions[currentStatusNumber - 1]?.subQuestion}</p>
          <form className="mt-3.5 gap-4 grid grid-cols-2">
            {questions[currentStatusNumber - 1]?.choices.map((item) => {
              return (
                <label
                  key={item.id}
                  className="w-full p-6 transition border rounded-lg cursor-pointer bg-neutral-50 border-neutral-500 hover:bg-neutral-100 hover:border-neutral-900 has-checked:bg-blue-50 has-checked:text-blue-900 has-checked:border-blue-200"
                >
                  <input
                    type="radio"
                    value={item.id}
                    name="roadmap"
                    className="hidden"
                    onChange={() => setSelectedValue(item)}
                    checked={
                      answers?.find(
                        (a) => a.questionNumber === currentStatusNumber
                      )?.choiceId === item.id || selectedValue?.id === item.id
                    }
                  />
                  <span>{item.name}</span>
                </label>
              );
            })}

            <div className="col-span-2 text-right">
              <Button
                type="button"
                className="w-44"
                onClick={() =>
                  selectedValue && handleChoiceSubmit(selectedValue)
                }
                disabled={disableBtn()}
              >
                계속하기
              </Button>
            </div>
          </form>
        </section>
      )}
    </>
  );
}
