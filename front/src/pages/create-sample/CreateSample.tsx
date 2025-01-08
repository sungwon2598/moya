import React, { useState } from "react";
import { PlusCircle } from "lucide-react";
import { useModal } from "@shared/hooks/useModal";
import CreateRoadmapModal from "@pages/create-sample/CreateRoadmapModal";
import {
    RoadmapRequest,
    WeeklyRoadmapResponse,
    RoadMapSimpleDto,
    roadmapApiService
} from "@core/config/roadmapApiConfig";

const CreateSample: React.FC = () => {
    const { showModal } = useModal();
    const [roadmapRequest, setRoadmapRequest] = useState<RoadmapRequest>({
        mainCategory: "",
        subCategory: "",
        currentLevel: "1",
        duration: 1
    });

    const [roadmapResponse, setRoadmapResponse] = useState<WeeklyRoadmapResponse | null>(null);
    const [roadmapList, setRoadmapList] = useState<RoadMapSimpleDto[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    // 로드맵 생성 함수
    const handleGenerateRoadmap = async (request: RoadmapRequest) => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await roadmapApiService.generateWeeklyRoadmap(request);
            setRoadmapResponse(response);
        } catch (err) {
            setError(err instanceof Error ? err.message : '로드맵 생성에 실패했습니다.');
        } finally {
            setIsLoading(false);
        }
    };

    // 카테고리별 로드맵 목록 조회 함수
    const fetchCategoryRoadmaps = async (categoryId: number) => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await roadmapApiService.getCategoryRoadmaps(categoryId);
            setRoadmapList(response);
        } catch (err) {
            setError(err instanceof Error ? err.message : '로드맵 목록 조회에 실패했습니다.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="p-6 min-h-screen bg-gray-50">
            <div className="max-w-6xl mx-auto">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-2xl font-bold text-gray-800">로드맵 샘플 관리</h1>
                </div>

                {/* 샘플 목록 영역 */}
                <div className="bg-white rounded-lg shadow p-6 min-h-[600px] relative">
                    {isLoading ? (
                        <div className="flex flex-col items-center justify-center h-[500px] text-gray-500">
                            <p className="text-lg">로딩 중...</p>
                        </div>
                    ) : error ? (
                        <div className="flex flex-col items-center justify-center h-[500px] text-red-500">
                            <p className="text-lg">{error}</p>
                        </div>
                    ) : roadmapList.length === 0 ? (
                        <div className="flex flex-col items-center justify-center h-[500px] text-gray-500">
                            <p className="text-lg">아직 등록된 샘플이 없습니다.</p>
                            <p className="text-sm mt-2">우측 하단의 버튼을 눌러 새로운 샘플을 추가해보세요.</p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                            {/* TODO: 로드맵 리스트 표시 */}
                        </div>
                    )}

                    {/* 추가 버튼 - 박스 우측 하단에 위치 */}
                    <div className="absolute bottom-6 right-6">
                        <button
                            className="bg-blue-500 text-white p-4 rounded-full shadow-lg hover:bg-blue-600 transition-colors flex items-center gap-2"
                            onClick={() => showModal(<CreateRoadmapModal onSubmit={handleGenerateRoadmap} />)}
                            disabled={isLoading}
                        >
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