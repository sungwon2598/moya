import { useEffect, useState } from 'react';
import Title from '../../components/features/roadmap/Title.tsx';
import Select from '../../components/features/roadmap/Select/Select.tsx';
import { useRoadmapQuestions } from '@/features/roadmap/hooks/useRoadmapQuestions';
import Progress from '@/components/features/roadmap/Select/Progress.tsx';
// import { useAuth } from '@/components/features/auth/hooks/useAuth.ts';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/auth.ts';
export interface RoadmapQuestionStageType {
  currentStatusNumber: number;
}

export type AnswerItem = {
  questionNumber: number | string; //질문 인덱스 +1
  name?: string; //질문 id
  choiceId: string; // 선택 값 Id
  choiceValue: string | number; // 선택값
};

export default function CreatePage() {
  const navigate = useNavigate();
  // const { isAuthenticated: isLoggedIn } = useAuth();
  const { isLogin } = useAuthStore();

  //api요청
  const { questions, isLoading, error, setSelectedCategoryId } = useRoadmapQuestions();
  // 질문ID, 선택ID 저장
  const [answers, setAnswers] = useState<AnswerItem[] | null>([]);
  // 1번 질문부터, 현재 질문페이지 저장
  const [roadmapQuestionStage, setRoadmapQuestionStage] = useState<RoadmapQuestionStageType>({
    currentStatusNumber: 1,
  });

  const categoryQuestion = questions.find((q) => q.name === 'mainCategory');
  useEffect(() => {
    if (!isLogin) {
      navigate('/');
      return alert('로그인이 필요한 서비스입니다.');
    }
    if (categoryQuestion) {
      const categoryAnswer = answers?.find((answer) => answer.questionNumber === categoryQuestion.id);
      if (categoryAnswer) {
        setSelectedCategoryId(categoryAnswer.choiceId);
      }
    }
  }, [answers, categoryQuestion, setSelectedCategoryId]);

  if (isLoading) return <div>로딩중...</div>;
  if (error) return <div>{error.message}</div>;

  return (
    <div className="@container md:max-w-3/4 mx-auto w-11/12">
      <Title />
      <Progress TotalQuestionsNumber={questions.length} roadmapQuestionStage={roadmapQuestionStage} />
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
