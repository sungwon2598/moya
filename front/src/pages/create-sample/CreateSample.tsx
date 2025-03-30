import React, { useState } from 'react';
import { PlusCircle } from 'lucide-react';
import { useModal } from '@shared/hooks/useModal';
import CreateRoadmapModal from '@pages/create-sample/CreateRoadmapModal';
import {
  RoadmapRequest,
  WeeklyRoadmapResponse,
  RoadMapSimpleDto,
  roadmapApiService,
} from '@core/config/roadmapApiConfig';
import ViewRoadmapModal from '@pages/create-sample/ViewRoadmapModal.tsx';

const CreateSample: React.FC = () => {
  const { showModal } = useModal();
  // const [roadmapRequest, setRoadmapRequest] = useState<RoadmapRequest>({
  //     mainCategory: "",
  //     subCategory: "",
  //     currentLevel: "1",
  //     duration: 1
  // });

  const [roadmapResponse, setRoadmapResponse] = useState<WeeklyRoadmapResponse | null>(null);
  const [roadmapList] = useState<RoadMapSimpleDto[]>([]); // fetchCategoryRoadmaps 구현 시 setRoadmapList 추가
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [hasNewResponse, setHasNewResponse] = useState<boolean>(false);

  // 로드맵 생성 함수
  const handleGenerateRoadmap = async (request: RoadmapRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await roadmapApiService.generateWeeklyRoadmap(request);
      setRoadmapResponse(response);
      setHasNewResponse(true); // 새로운 응답이 도착하면 true로 설정
    } catch (err) {
      setError(err instanceof Error ? err.message : '로드맵 생성에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 카테고리별 로드맵 목록 조회 함수
  // const fetchCategoryRoadmaps = async (categoryId: number) => {
  //     setIsLoading(true);
  //     setError(null);
  //     try {
  //         const response = await roadmapApiService.getCategoryRoadmaps(categoryId);
  //         setRoadmapList(response);
  //     } catch (err) {
  //         setError(err instanceof Error ? err.message : '로드맵 목록 조회에 실패했습니다.');
  //     } finally {
  //         setIsLoading(false);
  //     }
  // };

  return (
    <div className="min-h-screen p-6 bg-gray-50">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-2xl font-bold text-gray-800">로드맵 샘플 관리</h1>
        </div>

        {/* 샘플 목록 영역 */}
        <div className="relative min-h-[600px] rounded-lg bg-white p-6 shadow">
          {isLoading ? (
            <div className="flex h-[500px] flex-col items-center justify-center text-gray-500">
              <p className="text-lg">로딩 중...</p>
            </div>
          ) : error ? (
            <div className="flex h-[500px] flex-col items-center justify-center text-red-500">
              <p className="text-lg">{error}</p>
            </div>
          ) : roadmapList.length === 0 ? (
            <div className="flex h-[500px] flex-col items-center justify-center text-gray-500">
              <p className="text-lg">아직 등록된 샘플이 없습니다.</p>
              <p className="mt-2 text-sm">우측 하단의 버튼을 눌러 새로운 샘플을 추가해보세요.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">{/* TODO: 로드맵 리스트 표시 */}</div>
          )}

          {/* 추가 버튼들 영역 */}
          <div className="absolute flex gap-4 bottom-6 right-6">
            {roadmapResponse && (
              <button
                className="flex items-center gap-2 p-4 text-gray-800 transition-colors bg-white border border-gray-200 rounded-full shadow-lg hover:bg-gray-50"
                onClick={() => {
                  setHasNewResponse(false); // 확인 시 false로 설정
                  showModal(<ViewRoadmapModal roadmapResponse={roadmapResponse} />);
                }}>
                <span className="font-medium">샘플 확인</span>
                {hasNewResponse && (
                  <div className="flex items-center justify-center w-6 h-6 text-sm text-white bg-red-500 rounded-full">
                    1
                  </div>
                )}
              </button>
            )}
            <button
              className="flex items-center gap-2 p-4 text-white transition-colors bg-blue-500 rounded-full shadow-lg hover:bg-blue-600"
              onClick={() => showModal(<CreateRoadmapModal onSubmit={handleGenerateRoadmap} />)}
              disabled={isLoading}>
              <PlusCircle size={24} />
              <span className="font-medium">샘플 추가</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateSample;
