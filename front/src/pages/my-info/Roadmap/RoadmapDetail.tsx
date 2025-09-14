import { useState, useRef } from 'react';
import { Button } from '@/components';
import DayStudyPlan from '@/components/features/roadmap/DayStudyPlan';
import RotatingMessage from '@/components/features/roadmap/RotatingMessage';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/shared/ui/accordion';
import { useRoadmapDetail } from '@/features/myinfo/api/getRoadmap';
import { usePostWorksheetsCreate } from '@/features/roadmap/api/createRoadmap';
import { useModal } from '@/shared/hooks/useModal';
import { Day, Week } from '@/types/roadmap.types';
import { useParams, useSearchParams } from 'react-router-dom';
import { toast } from 'sonner';

export default function RoadmapDetail() {
  const { roadmapId } = useParams<{ roadmapId: string }>();
  const [searchParams] = useSearchParams();
  const mainCategory = searchParams.get('mainCategory');
  const subCategory = searchParams.get('subCategory');
  const { showModal } = useModal();
  const { data: roadmapDetailData, isLoading, isError } = useRoadmapDetail(Number(roadmapId));
  const { mutate, isPending } = usePostWorksheetsCreate();
  const [isCreatingWorksheets, setIsCreatingWorksheets] = useState(false);
  const createButtonRef = useRef<HTMLButtonElement>(null);

  const handleCreateAllWorksheets = async () => {
    if (!mainCategory || !subCategory) {
      toast.error('카테고리가 올바르지 않습니다.');
      return;
    }

    setIsCreatingWorksheets(true);

    mutate(
      {
        id: Number(roadmapId),
        mainCategory,
        subCategory,
      },
      {
        onSuccess: () => {
          setIsCreatingWorksheets(false);
          toast.success('워크시트가 성공적으로 생성되었습니다.');
        },
        onError: (error) => {
          setIsCreatingWorksheets(false);
          toast('워크시트 생성 실패', {
            description: '',
          });
          console.error('API 요청 중 오류 발생:', error);
        },
      }
    );
  };

  const handleScrollToCreateButton = () => {
    createButtonRef.current?.scrollIntoView({
      behavior: 'smooth',
      block: 'center',
    });
  };

  const handleShowApplicants = (dailyPlans: Day, week: number) => {
    showModal(<DayStudyPlan dailyPlans={dailyPlans} />, {
      title: (
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:gap-3">
          <p className="text-sm sm:text-base">
            {week}주차 {dailyPlans.day}일 :
          </p>
          <p className="text-sm font-medium sm:text-base">{dailyPlans.dailyKeyword}</p>
        </div>
      ),
      size: 'lg',
    });
  };

  if (isLoading) {
    return (
      <div className="flex min-h-64 items-center justify-center px-4 text-gray-600 dark:text-gray-300">
        불러오는 중...
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex min-h-64 items-center justify-center px-4 text-red-600 dark:text-red-400">
        에러가 발생했어요!
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-4xl p-3 sm:p-4 lg:p-6">
      <div className="z-20 mb-6 rounded-xl border border-white/30 bg-white/10 p-4 shadow-lg backdrop-blur-md sm:mb-8 sm:rounded-2xl sm:p-6 dark:border-white/20 dark:bg-white/5">
        <div className="mb-4 text-center">
          <h1 className="mb-1 break-words text-xl font-bold text-blue-900 sm:text-2xl lg:text-3xl dark:text-blue-100">
            {mainCategory}
          </h1>
          <h2 className="break-words text-base text-blue-700 sm:text-lg lg:text-xl dark:text-blue-300">
            {subCategory}
          </h2>
        </div>

        {roadmapDetailData?.curriculumEvaluation && (
          <div className="mb-4 rounded-lg bg-white/20 p-3 backdrop-blur-md sm:rounded-xl sm:p-4 dark:bg-white/10">
            <h3 className="mb-2 text-xs font-bold text-blue-900 sm:text-sm dark:text-blue-100">학습 가이드</h3>
            <p className="text-xs leading-relaxed text-blue-800 sm:text-sm dark:text-blue-200">
              {roadmapDetailData.curriculumEvaluation}
            </p>
          </div>
        )}

        {roadmapDetailData?.overallTips?.length > 0 && <RotatingMessage data={roadmapDetailData} />}

        <div className="mt-4 flex justify-center">
          <Button
            ref={createButtonRef}
            onClick={handleCreateAllWorksheets}
            disabled={isCreatingWorksheets || isPending}
            className="flex cursor-pointer items-center gap-2 rounded-lg bg-blue-600 px-6 py-3 text-sm font-medium text-white transition-colors hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-gray-400 sm:rounded-xl sm:px-8 sm:py-4 sm:text-base dark:bg-blue-500 dark:hover:bg-blue-600 dark:disabled:bg-gray-600">
            {isCreatingWorksheets || isPending ? (
              <>
                <div className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></div>
                전체 워크시트 생성중...
              </>
            ) : (
              <>
                <span>📝</span> 전체 워크시트 생성
              </>
            )}
          </Button>
        </div>
      </div>

      {/* Weekly Plan */}
      <div className="space-y-4 sm:space-y-6">
        {roadmapDetailData?.weeklyPlans?.map((weeklyPlan: Week, index: number) => (
          <div
            key={index}
            className="rounded-xl border border-white/20 bg-white/10 shadow-lg backdrop-blur-md sm:rounded-2xl dark:border-white/20 dark:bg-white/5">
            <Accordion type="single" collapsible>
              <AccordionItem value={`item-${index}`} className="border-none">
                <AccordionTrigger className="border-b border-white/10 bg-gradient-to-r from-white/30 to-white/10 px-3 py-3 backdrop-blur-md sm:px-6 sm:py-4 dark:from-white/10 dark:to-white/5">
                  <div className="flex w-full items-center justify-between">
                    <div className="flex items-center gap-2 sm:gap-3">
                      <div className="flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-xs font-bold text-white sm:h-8 sm:w-8 sm:text-sm">
                        {weeklyPlan.week}
                      </div>
                      <div className="text-left">
                        <p className="text-sm font-bold text-gray-800 sm:text-base dark:text-white">
                          {weeklyPlan.week}주차
                        </p>
                        <p className="break-words text-xs font-medium text-blue-600 sm:text-sm dark:text-blue-300">
                          {weeklyPlan.weeklyKeyword}
                        </p>
                      </div>
                    </div>
                  </div>
                </AccordionTrigger>

                <AccordionContent className="bg-transparent p-0">
                  <div className="grid gap-3 p-3 sm:gap-4 sm:p-6">
                    {weeklyPlan.dailyPlans.map((dailyPlans: Day, dailyIndex: number) => {
                      const isWorksheetEmpty = !dailyPlans.worksheet || dailyPlans.worksheet.trim() === '';
                      const isWorksheetCreating = isCreatingWorksheets || isPending;
                      const hasAnyWorksheet = roadmapDetailData?.weeklyPlans?.some((week: Week) =>
                        week.dailyPlans.some((day: Day) => day.worksheet && day.worksheet.trim() !== '')
                      );

                      return (
                        <div
                          key={dailyIndex}
                          className="rounded-xl border border-white/20 bg-white/10 p-3 shadow-sm backdrop-blur-md transition-shadow hover:shadow-lg sm:rounded-2xl sm:p-4 dark:border-white/20 dark:bg-white/5">
                          <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-center sm:gap-0">
                            <div className="flex items-center gap-2 sm:gap-3">
                              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-orange-100 text-sm font-bold text-orange-600 sm:h-10 sm:w-10 sm:rounded-xl sm:text-base">
                                {dailyPlans.day}
                              </div>
                              <div>
                                <p className="text-sm font-semibold text-gray-800 sm:text-base dark:text-white">
                                  {dailyPlans.day}일차
                                </p>
                                <p className="break-words text-xs font-medium text-orange-600 sm:text-sm dark:text-orange-300">
                                  {dailyPlans.dailyKeyword}
                                </p>
                              </div>
                            </div>

                            {isWorksheetEmpty && !hasAnyWorksheet && !isWorksheetCreating ? (
                              <Button
                                onClick={handleScrollToCreateButton}
                                className="flex w-full items-center justify-center gap-2 rounded-lg bg-blue-500 px-3 py-2 text-xs font-medium text-white transition-colors hover:bg-blue-600 sm:w-auto sm:rounded-xl sm:px-4 sm:text-sm dark:bg-blue-400 dark:hover:bg-blue-500">
                                <span>✏️</span> 워크시트를 생성해주세요
                              </Button>
                            ) : isWorksheetEmpty || isWorksheetCreating ? (
                              <Button
                                disabled
                                className="flex w-full cursor-not-allowed items-center justify-center gap-2 rounded-lg bg-gray-400 px-3 py-2 text-xs font-medium text-white sm:w-auto sm:rounded-xl sm:px-4 sm:text-sm">
                                <div className="h-3 w-3 animate-spin rounded-full border-2 border-white border-t-transparent sm:h-4 sm:w-4"></div>
                                학습지 생성중
                              </Button>
                            ) : (
                              <Button
                                onClick={() => handleShowApplicants(dailyPlans, weeklyPlan.week)}
                                className="flex w-full items-center justify-center gap-2 rounded-lg bg-green-600 px-3 py-2 text-xs font-medium text-white transition-colors hover:bg-green-700 sm:w-auto sm:rounded-xl sm:px-4 sm:text-sm dark:bg-green-500 dark:hover:bg-green-600">
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
    </div>
  );
}
