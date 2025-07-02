import { Button } from '@/components';
import DayStudyPlan from '@/components/features/roadmap/DayStudyPlan';
import RotatingMessage from '@/components/features/roadmap/RotatingMessage';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/shared/ui/accordion';
import { useRoadmapDetail } from '@/features/myinfo/api/getRoadmap';
import { useModal } from '@/shared/hooks/useModal';
import { Day, Week } from '@/types/roadmap.types';
import { useParams, useSearchParams } from 'react-router-dom';

export default function RoadmapDetail() {
  const { roadmapId } = useParams<{ roadmapId: string }>();
  const [searchParams] = useSearchParams();
  const mainCategory = searchParams.get('mainCategory');
  const subCategory = searchParams.get('subCategory');
  const { showModal } = useModal();
  const { data: roadmapDetailData, isLoading, isError } = useRoadmapDetail(Number(roadmapId));

  const handleShowApplicants = (dailyPlans: Day, week: number) => {
    showModal(<DayStudyPlan dailyPlans={dailyPlans} />, {
      title: (
        <div className="flex items-center gap-3">
          <p>
            {week}주차 {dailyPlans.day}일 :
          </p>
          <p>{dailyPlans.dailyKeyword}</p>
        </div>
      ),
      size: 'lg',
    });
  };

  if (isLoading) {
    return (
      <div className="flex min-h-64 items-center justify-center text-gray-600 dark:text-gray-300">불러오는 중...</div>
    );
  }

  if (isError) {
    return (
      <div className="flex min-h-64 items-center justify-center text-red-600 dark:text-red-400">에러가 발생했어요!</div>
    );
  }

  return (
    <div className="mx-auto max-w-4xl px-4">
      {/* Header */}
      <div className="mb-8 rounded-2xl border border-white/30 bg-white/10 p-6 shadow-lg backdrop-blur-md dark:border-white/20 dark:bg-white/5">
        <div className="mb-4 text-center">
          <h1 className="mb-1 text-2xl font-bold text-blue-900 dark:text-blue-100">{mainCategory}</h1>
          <h2 className="text-lg text-blue-700 dark:text-blue-300">{subCategory}</h2>
        </div>

        {roadmapDetailData?.curriculumEvaluation && (
          <div className="z-10 mb-4 rounded-xl bg-white/20 p-4 backdrop-blur-md dark:bg-white/10">
            <h3 className="mb-2 text-sm font-bold text-blue-900 dark:text-blue-100">학습 가이드</h3>
            <p className="text-sm text-blue-800 dark:text-blue-200">{roadmapDetailData.curriculumEvaluation}</p>
          </div>
        )}

        {roadmapDetailData?.overallTips?.length > 0 && <RotatingMessage data={roadmapDetailData} />}
      </div>

      {/* Weekly Plan */}
      <div className="space-y-6">
        {roadmapDetailData?.weeklyPlans?.map((weeklyPlan: Week, index: number) => (
          <div
            key={index}
            className="rounded-2xl border border-white/20 bg-white/10 shadow-lg backdrop-blur-md dark:border-white/20 dark:bg-white/5">
            <Accordion type="single" collapsible>
              <AccordionItem value={`item-${index}`} className="border-none">
                <AccordionTrigger className="border-b border-white/10 bg-gradient-to-r from-white/30 to-white/10 px-6 py-4 backdrop-blur-md dark:from-white/10 dark:to-white/5">
                  <div className="flex w-full items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-600 text-sm font-bold text-white">
                        {weeklyPlan.week}
                      </div>
                      <div className="text-left">
                        <p className="font-bold text-gray-800 dark:text-white">{weeklyPlan.week}주차</p>
                        <p className="text-sm font-medium text-blue-600 dark:text-blue-300">
                          {weeklyPlan.weeklyKeyword}
                        </p>
                      </div>
                    </div>
                    <Button
                      onClick={(e) => e.stopPropagation()}
                      className="rounded-xl bg-purple-600/80 px-4 py-2 font-medium text-white transition-colors hover:bg-purple-700 dark:bg-purple-500/60 dark:hover:bg-purple-600">
                      📝 스터디 상세 만들기
                    </Button>
                  </div>
                </AccordionTrigger>

                <AccordionContent className="bg-transparent p-0">
                  <div className="grid gap-4 p-6">
                    {weeklyPlan.dailyPlans.map((dailyPlans: Day, dailyIndex: number) => {
                      const isWorksheetEmpty = !dailyPlans.worksheet || dailyPlans.worksheet.trim() === '';
                      return (
                        <div
                          key={dailyIndex}
                          className="rounded-2xl border border-white/20 bg-white/10 p-4 shadow-sm backdrop-blur-md transition-shadow hover:shadow-lg dark:border-white/20 dark:bg-white/5">
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-3">
                              <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-orange-100 font-bold text-orange-600">
                                {dailyPlans.day}
                              </div>
                              <div>
                                <p className="font-semibold text-gray-800 dark:text-white">{dailyPlans.day}일차</p>
                                <p className="text-sm font-medium text-orange-600 dark:text-orange-300">
                                  {dailyPlans.dailyKeyword}
                                </p>
                              </div>
                            </div>

                            {isWorksheetEmpty ? (
                              <Button
                                disabled
                                className="flex cursor-not-allowed items-center gap-2 rounded-xl bg-gray-400 px-4 py-2 font-medium text-white">
                                <div className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></div>
                                학습지 생성중
                              </Button>
                            ) : (
                              <Button
                                onClick={() => handleShowApplicants(dailyPlans, weeklyPlan.week)}
                                className="flex items-center gap-2 rounded-xl bg-green-600 px-4 py-2 font-medium text-white transition-colors hover:bg-green-700 dark:bg-green-500 dark:hover:bg-green-600">
                                <span>📖</span> 공부하러가기
                              </Button>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </AccordionContent>
              </AccordionItem>
            </Accordion>
          </div>
        ))}
      </div>

      {/* Footer */}
      <div className="mt-12 border-t border-white/10 pt-6 text-center text-sm text-gray-500 dark:text-gray-400">
        💡 매일 꾸준히 학습하여 목표를 달성해보세요!
      </div>
    </div>
  );
}
