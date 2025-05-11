import { Button } from '@/components';
import WeeklyPlanCard from '@/components/features/roadmap/WeeklyPlan/WeeklyPlanCard';
// import { usePostRoadmapCreate } from '@/features/roadmap/api/createRoadmap';
import { useRotatingMessage } from '@/features/roadmap/hooks/useRotatingMessage';
import { RoadmapData } from '@/types/roadmap.types';
import { useNavigate } from 'react-router-dom';

export default function WeeklyPlan() {
  const navigate = useNavigate();
  // const { data } = usePostRoadmapCreate();
  const data: RoadmapData = {
    weeklyPlans: [
      {
        week: 1,
        weeklyKeyword: 'C# 언어의 기본 문법 이해',
        dailyPlans: [
          {
            day: 1,
            dailyKeyword: 'C# 소개 및 개발 환경 설정',
          },
          {
            day: 2,
            dailyKeyword: '기본 데이터 타입과 변수 선언',
          },
          {
            day: 3,
            dailyKeyword: '연산자와 표현식',
          },
          {
            day: 4,
            dailyKeyword: '조건문과 제어 흐름',
          },
          {
            day: 5,
            dailyKeyword: '반복문 기본 이해',
          },
          {
            day: 6,
            dailyKeyword: '배열과 리스트',
          },
          {
            day: 7,
            dailyKeyword: '메서드와 함수 기초',
          },
        ],
      },
      {
        week: 2,
        weeklyKeyword: '객체 지향 프로그래밍 기본 이해',
        dailyPlans: [
          {
            day: 1,
            dailyKeyword: '클래스와 객체',
          },
          {
            day: 2,
            dailyKeyword: '속성과 메서드',
          },
          {
            day: 3,
            dailyKeyword: '생성자와 소멸자',
          },
          {
            day: 4,
            dailyKeyword: '상속과 다형성',
          },
          {
            day: 5,
            dailyKeyword: '인터페이스와 추상 클래스',
          },
          {
            day: 6,
            dailyKeyword: '예외 처리',
          },
          {
            day: 7,
            dailyKeyword: '기본 파일 입출력',
          },
        ],
      },
    ],
    overallTips: [
      'C# 공식 문서와 튜토리얼을 적극 활용하세요.',
      '각 개념을 실습 코딩으로 체화하세요.',
      '질문이 생기면 커뮤니티에서 토론을 통해 해결하세요.',
    ],
    curriculumEvaluation: '이 로드맵은 C#에 대한 초급 학습자가 기본 개념을 이해하기에 적합합니다.',
    hasRestrictedTopics: '없음',
  };
  const { currentMessage } = useRotatingMessage(data && data?.overallTips?.length > 0 ? data.overallTips : [''], 5000);

  return (
    <div className="@container overflow-hidden px-4">
      <h4 className="py-2">{data?.curriculumEvaluation}</h4>
      {data?.overallTips && data.overallTips.length > 0 && (
        <div className="group relative pb-2">
          <div className="rounded bg-neutral-100 p-2">
            <p className="mb-1 font-semibold">📚 Tip.</p>
            <p>{currentMessage as string}</p>
          </div>
          <div className="absolute right-1/2 top-1/2 z-10 hidden w-full -translate-x-1/2 rounded border border-neutral-400 bg-neutral-200 p-2 shadow-2xl group-hover:block">
            {data.overallTips.map((tip: string, index: number) => (
              <p
                key={index}
                className={`${currentMessage === tip ? 'text-moya-primary font-semibold' : 'text-neutral-400'}`}>
                {tip}
              </p>
            ))}
          </div>
        </div>
      )}

      <div className="mb-12 border-neutral-200">
        <WeeklyPlanCard weeks={data?.weeklyPlans} />
      </div>
      <div className="my-4 text-center">
        <Button onClick={() => navigate('/my-info/roadmap')}>생성 완료</Button>
      </div>
    </div>
  );
}
