import React, { useState } from "react";
import { RoadmapRequest, RoadmapResponse } from "@pages/create-sample/types.ts";
import { PlusCircle } from "lucide-react";
import { useModal } from "@shared/hooks/useModal";
import CreateRoadmapModal from "@pages/create-sample/CreateRoadmapModal";

const CreateSample: React.FC = () => {
    const { showModal } = useModal();
    const [roadmapRequest, setRoadmapRequest] = useState<RoadmapRequest>({
        mainCategory: "",
        subCategory: "",
        currentLevel: "1",
        duration: 1
    });

    const [roadmapResponse, setRoadmapResponse] = useState<RoadmapResponse | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    return (
        <div className="p-6 min-h-screen bg-gray-50">
            <div className="max-w-6xl mx-auto">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-2xl font-bold text-gray-800">로드맵 샘플 관리</h1>
                </div>

                {/* 샘플 목록 영역 */}
                <div className="bg-white rounded-lg shadow p-6 min-h-[600px] relative">
                    {/* 빈 상태 메시지 */}
                    <div className="flex flex-col items-center justify-center h-[500px] text-gray-500">
                        <p className="text-lg">아직 등록된 샘플이 없습니다.</p>
                        <p className="text-sm mt-2">우측 하단의 버튼을 눌러 새로운 샘플을 추가해보세요.</p>
                    </div>

                    {/* 추가 버튼 - 박스 우측 하단에 위치 */}
                    <div className="absolute bottom-6 right-6">
                        <button
                            className="bg-blue-500 text-white p-4 rounded-full shadow-lg hover:bg-blue-600 transition-colors flex items-center gap-2"
                            onClick={() => showModal(<CreateRoadmapModal />)}
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