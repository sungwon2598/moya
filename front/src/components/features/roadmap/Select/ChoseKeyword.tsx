import { Question } from '@/features/roadmap/hooks/useRoadmapQuestions';
import { AnswerItem, RoadmapQuestionStageType } from '@/pages/roadmap/CreatePage';
import { type Dispatch, type SetStateAction } from 'react';
import { Button } from '@/components/shared/ui/button';
import { usePostRoadmapCreate } from '@/features/roadmap/api/createRoadmap';
import { useNavigate } from 'react-router-dom';
import { SelectedValue } from './Select';
import { toast } from 'sonner';

const selectBTN =
  'inline-block px-4 py-2  mr-2 bg-blue-100/60 rounded min-w-32 min-h-10 hover:bg-blue-200/40 transition-colors';
const pBTN = 'mt-2';
interface ChoseKeywordProp {
  AllQuestionsCompleted: boolean;
  answers: AnswerItem[] | null;
  setAnswers: Dispatch<SetStateAction<AnswerItem[] | null>>;
  questions: Question[];
  setRoadmapQuestionStage: Dispatch<SetStateAction<RoadmapQuestionStageType>>;
  setSelectedValue: (item: SelectedValue | null) => void;
}
export default function ChoseKeyword({
  AllQuestionsCompleted,
  answers,
  setAnswers,
  questions,
  setRoadmapQuestionStage,
  setSelectedValue,
}: ChoseKeywordProp) {
  const navigate = useNavigate();

  const { mutate } = usePostRoadmapCreate();

  const navigateToQuestion = (questionName: string) => {
    const question = questions.find((q) => q.name === questionName);
    if (!question) return;
    const allPreviousQuestionsAnswered = Array.from({ length: question.id - 1 }, (_, i) => i + 1).every((id) =>
      answers?.some((answer) => answer.questionNumber === id)
    );
    if (allPreviousQuestionsAnswered) {
      setRoadmapQuestionStage({ currentStatusNumber: question.id });
      setSelectedValue(null);
    } else {
      toast('이전 질문에 대답해주세요.', {
        description: '',
      });
    }
  };

  const handlerReset = () => {
    navigateToQuestion('learningObjective');
    setAnswers(null);
  };
  const getFormData = () => {
    return {
      mainCategory: String(
        answers?.find((answer) => answer.name === 'mainCategory' && answer.choiceId !== 'custom')?.choiceValue ?? ''
      ),
      etc1: String(
        answers?.find((answer) => answer.name === 'mainCategory' && answer.choiceId === 'custom')?.choiceValue ?? ''
      ),
      subCategory: String(
        answers?.find((answer) => answer.name === 'subCategory' && answer.choiceId !== 'custom')?.choiceValue ?? ''
      ),
      etc2: String(
        answers?.find((answer) => answer.name === 'subCategory' && answer.choiceId === 'custom')?.choiceValue ?? ''
      ),
      currentLevel: String(answers?.find((answer) => answer.name === 'currentLevel')?.choiceId ?? '1'),
      duration: Number(answers?.find((answer) => answer.name === 'duration')?.choiceValue),
      learningObjective: String(answers?.find((answer) => answer.name === 'learningObjective')?.choiceValue),
    };
  };

  const handleRoadmapCreateSubmit = async () => {
    const formData = getFormData();
    mutate(formData, {
      onError: (error) => {
        toast('로드맵 생성 실패', {
          description: '',
        });
        console.error('API 요청 중 오류 발생:', error);
      },
    });
    navigate('/roadmap/pending');
  };

  return (
    <article
      className={`${AllQuestionsCompleted ? 'col-span-2 text-center' : ''} mb-12 rounded-2xl bg-neutral-100/10 p-8 text-center text-3xl font-bold md:text-left`}>
      <div className={`${AllQuestionsCompleted ? 'flex items-center justify-center gap-2' : ''}`}>
        <p>나는</p>
        <p className={`${pBTN}`}>
          <button className={`${selectBTN}`} onClick={() => navigateToQuestion('learningObjective')}>
            {String(answers?.find((answer) => answer.name === 'learningObjective')?.choiceValue ?? '')}
          </button>
          을 위해
        </p>
      </div>
      <div className={`${AllQuestionsCompleted ? 'flex items-center justify-center gap-2' : ''} mt-8`}>
        <p>
          <button className={`${selectBTN}`} onClick={() => navigateToQuestion('duration')}>
            {getFormData().duration > 0 ? `${getFormData().duration}주` : ''}
          </button>
          동안
        </p>
        <p className={`${pBTN}`}>
          <button className={`${selectBTN}`} onClick={() => navigateToQuestion('mainCategory')}>
            {getFormData().mainCategory.length > 0 ? getFormData().mainCategory : getFormData().etc1}
          </button>
          의
        </p>
      </div>
      <div className={`${AllQuestionsCompleted ? 'flex items-center justify-center gap-2' : ''} `}>
        <p className={`${pBTN}`}>
          <button className={`${selectBTN}`} onClick={() => navigateToQuestion('subCategory')}>
            {getFormData().subCategory.length > 0 ? getFormData().subCategory : getFormData().etc2}
          </button>
          을/를
        </p>
        <p className={`${pBTN}`}>
          <button className={`${selectBTN}`} onClick={() => navigateToQuestion('subCategory')}>
            {answers?.find((answer) => answer.name === 'currentLevel')?.choiceValue}
          </button>
          수준으로
        </p>
      </div>
      <p className={`text-center ${pBTN}`}>공부하고 싶어요.</p>
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
