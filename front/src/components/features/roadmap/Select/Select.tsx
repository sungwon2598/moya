import { Button } from '@/components/shared/ui/button';
import { Question } from '@/features/roadmap/hooks/useRoadmapQuestions';
import { AnswerItem, RoadmapQuestionStageType } from '@/pages/roadmap/CreatePage';
import { Fragment, useEffect, useState } from 'react';
import type { ChangeEvent, Dispatch, SetStateAction } from 'react';
import ChoseKeyword from './ChoseKeyword';
import { Slider } from '@/components/shared/ui/slider';

interface SelectProp {
  answers: AnswerItem[] | null;
  setAnswers: Dispatch<SetStateAction<AnswerItem[] | null>>;
  questions: Question[];
  roadmapQuestionStage: RoadmapQuestionStageType;
  setRoadmapQuestionStage: Dispatch<SetStateAction<RoadmapQuestionStageType>>;
}
export interface SelectedValue {
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
  const [customInputValue, setCustomInputValue] = useState<string | number>('');
  const [customSliderValue, setCustomSliderValue] = useState<number>(20);
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, []);
  const handleChoiceSubmit = (choiceId: SelectedValue) => {
    const currentQuestion = questions[currentStatusNumber - 1];
    setAnswers((prev) => {
      const newAnswers = [...(prev ?? [])];
      const existingAnswerIndex = newAnswers.findIndex((answer) => answer.questionNumber === currentStatusNumber);
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
    setCustomInputValue('');
    setRoadmapQuestionStage((prevStage) => ({
      ...prevStage,
      currentStatusNumber: prevStage.currentStatusNumber + 1,
    }));
  };

  const disableBtn = () => {
    if (selectedValue?.id === 'custom') {
      if (currentStatusNumber === 2) {
        return typeof selectedValue.name !== 'number' || Number(selectedValue.name) <= 0;
      } else {
        return !String(customInputValue).trim();
      }
    }
    if (answers?.some((item) => item.questionNumber === currentStatusNumber)) {
      return false;
    }
    return !selectedValue;
  };

  const AllQuestionsCompleted = answers?.length === questions.length;
  const handleCustomInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setCustomInputValue(value);
    if (selectedValue?.id === 'custom') {
      setSelectedValue({ id: 'custom', name: value });
    }
  };
  const handleSliderChange = (value: number[]) => {
    const val = value[0];
    setCustomSliderValue(val);
    const currentQuestion = questions[currentStatusNumber - 1];

    setAnswers((prev) => {
      const newAnswers = [...(prev ?? [])];
      const existingAnswerIndex = newAnswers.findIndex((answer) => answer.questionNumber === currentStatusNumber);

      if (existingAnswerIndex !== -1) {
        newAnswers[existingAnswerIndex] = {
          ...newAnswers[existingAnswerIndex],
          choiceId: 'custom',
          name: currentQuestion.name,
          choiceValue: val,
        };
      } else {
        newAnswers.push({
          questionNumber: currentStatusNumber,
          choiceId: 'custom',
          name: currentQuestion.name,
          choiceValue: val,
        });
      }

      return newAnswers;
    });
    setSelectedValue({ id: 'custom', name: val });
  };

  return (
    <>
      <ChoseKeyword
        answers={answers}
        setAnswers={setAnswers}
        questions={questions}
        setRoadmapQuestionStage={setRoadmapQuestionStage}
        AllQuestionsCompleted={AllQuestionsCompleted}
        setSelectedValue={setSelectedValue}
      />
      {!AllQuestionsCompleted && (
        <section>
          <h3>{questions[currentStatusNumber - 1]?.title}</h3>
          <p>{questions[currentStatusNumber - 1]?.subQuestion}</p>
          <form
            className="mt-3.5 grid grid-cols-2 gap-4"
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
                if (!disableBtn() && selectedValue) {
                  handleChoiceSubmit(selectedValue);
                }
              }
            }}>
            {questions[currentStatusNumber - 1]?.choices.map((item) => {
              return item.id === 'custom' ? (
                <Fragment key={item.id}>
                  {currentStatusNumber === 2 && (
                    <>
                      <h5>{customSliderValue}</h5>
                      <Slider
                        defaultValue={[customSliderValue]}
                        max={80}
                        step={1}
                        onValueChange={handleSliderChange}
                        className="col-span-2"
                      />
                    </>
                  )}
                  {currentStatusNumber !== 2 && (
                    <label
                      key={item.id}
                      className="has-checked:bg-blue-50 has-checked:text-blue-900 has-checked:border-blue-200 group cursor-pointer rounded-lg border border-neutral-500 bg-amber-200 bg-neutral-50 p-6 transition hover:border-neutral-900 hover:bg-neutral-100">
                      <>
                        <input
                          type="radio"
                          name="roadmap"
                          className="hidden"
                          onChange={() => setSelectedValue({ id: item.id, name: customInputValue })}
                          checked={
                            answers?.find((a) => a.questionNumber === currentStatusNumber)?.choiceId === item.id ||
                            selectedValue?.id === item.id
                          }
                        />
                        <input
                          type={currentStatusNumber === 2 ? 'number' : 'text'}
                          className="inline w-full bg-transparent focus:outline-none"
                          placeholder={item.name}
                          value={
                            selectedValue?.id === item.id
                              ? customInputValue
                              : (answers?.find(
                                  (a) => a.questionNumber === currentStatusNumber && a.choiceId === item.id
                                )?.choiceValue as string) || ''
                          }
                          onChange={handleCustomInputChange}
                          onClick={() => {
                            if (selectedValue?.id !== item.id) {
                              setSelectedValue({ id: item.id, name: customInputValue });
                            }
                          }}
                        />
                      </>
                    </label>
                  )}
                </Fragment>
              ) : (
                <label
                  key={item.id}
                  className="has-checked:bg-blue-50 has-checked:text-blue-900 has-checked:border-blue-200 w-full cursor-pointer rounded-lg border border-neutral-500 bg-neutral-50 p-6 transition hover:border-neutral-900 hover:bg-neutral-100">
                  <input
                    type="radio"
                    value={item.id}
                    name="roadmap"
                    className="hidden"
                    onChange={() => setSelectedValue(item)}
                    checked={
                      answers?.find((a) => a.questionNumber === currentStatusNumber)?.choiceId === item.id ||
                      selectedValue?.id === item.id
                    }
                  />
                  <span>
                    {item.name}
                    {currentStatusNumber === 2 && '일'}
                  </span>
                </label>
              );
            })}

            <div className="col-span-2 text-right">
              <Button
                type="button"
                className="w-44"
                onClick={() => handleChoiceSubmit(selectedValue!)}
                disabled={disableBtn()}>
                계속하기
              </Button>
            </div>
          </form>
        </section>
      )}
    </>
  );
}
