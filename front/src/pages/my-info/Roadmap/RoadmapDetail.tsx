import { Button } from '@/components';
import DayStudyPlan from '@/components/features/roadmap/DayStudyPlan';
import RotatingMessage from '@/components/features/roadmap/RotatingMessage';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/shared/ui/accordion';
import { useRoadmapDetail } from '@/features/myinfo/api/getRoadmap';
import { useModal } from '@/shared/hooks/useModal';
import { Day } from '@/types/roadmap.types';
import { User } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
const fakeData = {
  weeklyPlans: [
    {
      week: 1,
      weeklyKeyword: '롤 기본 이해 및 역할 학습',
      dailyPlans: [
        {
          day: 1,
          dailyKeyword: '롤 개요 및 인터페이스',
        },
        {
          day: 2,
          dailyKeyword: '챔피언 역할 및 기본 이론',
        },
        {
          day: 3,
          dailyKeyword: '주요 챔피언 스킬 메커니즘',
        },
        {
          day: 4,
          dailyKeyword: '맵 이해 및 포지셔닝',
        },
        {
          day: 5,
          dailyKeyword: '기본 전략 및 전투 개념',
        },
        {
          day: 6,
          dailyKeyword: '아이템 시스템과 빌드',
        },
        {
          day: 7,
          dailyKeyword: '간단한 경기 분석',
        },
      ],
    },
    {
      week: 2,
      weeklyKeyword: '라인별 심화 학습',
      dailyPlans: [
        {
          day: 1,
          dailyKeyword: '탑 라인 집중 학습',
        },
        {
          day: 2,
          dailyKeyword: '정글 동선 및 전략',
        },
        {
          day: 3,
          dailyKeyword: '미드 라인 챔피언 분석',
        },
        {
          day: 4,
          dailyKeyword: '바텀 듀오 플레이 이해',
        },
        {
          day: 5,
          dailyKeyword: '서포터 역할 및 워딩',
        },
        {
          day: 6,
          dailyKeyword: '라인전 전략 및 실습',
        },
        {
          day: 7,
          dailyKeyword: '라인별 경기 리뷰',
        },
      ],
    },
    {
      week: 3,
      weeklyKeyword: '게임 메커니즘 및 팀 플레이',
      dailyPlans: [
        {
          day: 1,
          dailyKeyword: '게임 메커니즘 심화',
        },
        {
          day: 2,
          dailyKeyword: '팀 구성 및 시너지 효과',
        },
        {
          day: 3,
          dailyKeyword: '오브젝트 컨트롤 및 타이밍',
        },
        {
          day: 4,
          dailyKeyword: '펜타킬의 순간 포착',
        },
        {
          day: 5,
          dailyKeyword: '가장 효과적인 커뮤니케이션',
        },
        {
          day: 6,
          dailyKeyword: '팀 경기 전략 실습',
        },
        {
          day: 7,
          dailyKeyword: '팀 플레이 분석',
        },
      ],
    },
    {
      week: 4,
      weeklyKeyword: '면접 대비 및 전략적 사고',
      dailyPlans: [
        {
          day: 1,
          dailyKeyword: '롤 면접 질문 분석',
        },
        {
          day: 2,
          dailyKeyword: '시뮬레이션 면접 및 피드백',
        },
        {
          day: 3,
          dailyKeyword: '전략적 사고 및 문제 해결',
        },
        {
          day: 4,
          dailyKeyword: '롤 사례 연구 및 분석',
        },
        {
          day: 5,
          dailyKeyword: '개인화된 전략 개발',
        },
        {
          day: 6,
          dailyKeyword: '최종 면접 준비',
        },
        {
          day: 7,
          dailyKeyword: '자신감 있게 발표하기',
        },
      ],
    },
  ],
  overallTips: [
    '매일 실습을 통해 얻은 지식을 즉시 적용하여 학습 효과를 극대화하세요.',
    '각 주말에 짧은 자기 점검을 통해 학습 내용을 복습하세요.',
  ],
  curriculumEvaluation:
    '이 로드맵은 롤의 다양한 측면을 폭넓게 다루고 있으며, 면접 준비에 필요한 심화 학습을 포함하고 있어 적합합니다.',
  hasRestrictedTopics: '없음',
};
export default function RoadmapDetail() {
  const { roadmapId } = useParams();
  const [searchParams] = useSearchParams();
  const mainCategory = searchParams.get('mainCategory');
  const subCategory = searchParams.get('subCategory');
  const { showModal } = useModal();
  const { data: roadmapDetailData, isLoading, isError } = useRoadmapDetail(Number(roadmapId));
  const [data, setData] = useState(fakeData);
  useEffect(() => {
    if (roadmapDetailData) {
      setData(roadmapDetailData);
    }
  }, []);
  const handleShowApplicants = (dailyPlans: Day) => {
    showModal(<DayStudyPlan dailyPlans={dailyPlans} />, {
      title: (
        <div className="flex items-center gap-3">
          {dailyPlans.day}
          {dailyPlans.dailyKeyword}
        </div>
      ),
      size: 'lg', // 모달 크기 (ModalProps에 정의된 크기)
    });
  };
  if (isLoading) return <div>불러오는 중...</div>;
  if (isError) return <div>에러가 발생했어요!</div>;
  return (
    <section className="container-roadmap">
      <div className="">
        <h3 className="text-neutral-950">{mainCategory}</h3>
        <h5 className="text-neutral-600">{subCategory}</h5>
        <p className="mb-6 text-sm text-neutral-500">{data.curriculumEvaluation}</p>
        {data?.overallTips && data.overallTips.length > 0 && <RotatingMessage data={data} />}
      </div>
      <div>
        <h6 className="mt-4">학습플랜</h6>
        {data.weeklyPlans &&
          data.weeklyPlans.length > 0 &&
          data.weeklyPlans.map((weeklyPlan) => (
            <Accordion type="single" collapsible>
              <AccordionItem value="item-1">
                <AccordionTrigger>
                  <div className="flex w-full items-center justify-between">
                    <p>
                      {weeklyPlan.week}주차 <span>{weeklyPlan.weeklyKeyword}</span>
                    </p>
                    <Button onClick={() => console.log('click')}>스터디 상세 만들기</Button>
                  </div>
                </AccordionTrigger>
                <AccordionContent className="rounded-lg bg-neutral-100">
                  {weeklyPlan.dailyPlans.map((dailyPlans) => (
                    <div className="p-2">
                      <p>
                        {dailyPlans.day}일차 <span>{dailyPlans.dailyKeyword}</span>
                      </p>
                      <Button onClick={() => handleShowApplicants(dailyPlans)}>공부하러가기</Button>
                    </div>
                  ))}
                </AccordionContent>
              </AccordionItem>
            </Accordion>
          ))}
      </div>
    </section>
  );
}
