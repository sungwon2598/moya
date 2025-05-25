import { Button } from '@/components';
import RotatingMessage from '@/components/features/roadmap/RotatingMessage';
import WeeklyPlanCard from '@/components/features/roadmap/WeeklyPlan/WeeklyPlanCard';
import { usePostRoadmapCreate } from '@/features/roadmap/api/createRoadmap';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
const fakeData = {
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
  curriculumEvaluation: '테스트데이터',
  hasRestrictedTopics: '없음',
};
export default function WeeklyPlan() {
  const navigate = useNavigate();
  const { data: roadmapData } = usePostRoadmapCreate();
  const [data, setData] = useState(fakeData);
  useEffect(() => {
    if (roadmapData) {
      setData(roadmapData);
    }
  }, []);

  return (
    <div className="@container overflow-hidden px-4">
      <h4 className="py-2">{data?.curriculumEvaluation}</h4>
      {data?.overallTips && data.overallTips.length > 0 && <RotatingMessage data={data} />}
      <div className="mb-12 border-neutral-200">
        <WeeklyPlanCard weeks={data?.weeklyPlans} />
      </div>
      <div className="my-4 text-center">
        <Button onClick={() => navigate('/my-info/roadmap')}>생성 완료</Button>
      </div>
    </div>
  );
}
