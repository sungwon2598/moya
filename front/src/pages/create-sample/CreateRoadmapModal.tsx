import React, { useState, useEffect } from "react";
import { useModal } from "@shared/hooks/useModal";
import { studyApiService } from "@core/config/studyApiConfig";
import { RoadmapRequest } from "@core/config/roadmapApiConfig";

interface Category {
    id: number;
    name: string;
    subCategories: Category[];
}

const CreateRoadmapModal: React.FC<{ onSubmit: (request: RoadmapRequest) => void }> = ({ onSubmit }) => {
    const { hideModal } = useModal();
    const [step, setStep] = useState(1);
    const [categories, setCategories] = useState<Category[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    // 선택된 값들을 저장할 상태
    const [selectedMainCategory, setSelectedMainCategory] = useState<Category | null>(null);
    const [selectedMiddleCategory, setSelectedMiddleCategory] = useState<Category | null>(null);
    const [formData, setFormData] = useState({
        topic: "",
        level: "1",
        duration: 1
    });

    // 카테고리 데이터 불러오기
    useEffect(() => {
        const fetchCategories = async () => {
            setIsLoading(true);
            try {
                const response = await studyApiService.getCategoriesHierarchy();
                setCategories(response);
            } catch (error) {
                console.error("카테고리 로드 실패:", error);
            }
            setIsLoading(false);
        };
        fetchCategories();
    }, []);

    // 최종 제출 처리
    const handleSubmit = () => {
        if (!selectedMiddleCategory) return;

        const request: RoadmapRequest = {
            mainCategory: selectedMiddleCategory.name,
            subCategory: formData.topic,
            currentLevel: formData.level,
            duration: formData.duration
        };

        onSubmit(request);
        hideModal();
    };

    // 단계별 UI 렌더링
    const renderStepContent = () => {
        switch (step) {
            case 1:
                return (
                    <div className="space-y-4">
                        <h3 className="text-lg font-medium mb-4">대분류 선택</h3>
                        <div className="flex flex-wrap gap-3">
                            <div className="bg-gray-50 p-4 rounded-lg">
                                <div className="flex flex-wrap gap-3">
                                    {categories.map((category) => (
                                        <div
                                            key={category.id}
                                            className={`px-4 py-2 rounded-full cursor-pointer transition-colors duration-200 ${
                                                selectedMainCategory?.id === category.id
                                                    ? 'bg-blue-100 text-blue-700'
                                                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                            }`}
                                            onClick={() => setSelectedMainCategory(category)}
                                        >
                                            {category.name}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                );

            case 2:
                return (
                    <div className="space-y-4">
                        <h3 className="text-lg font-medium mb-4">중분류 선택</h3>
                        <div className="flex flex-wrap gap-3">
                            <div className="bg-gray-50 p-4 rounded-lg">
                                <div className="flex flex-wrap gap-3">
                                    {selectedMainCategory?.subCategories.map((category) => (
                                        <div
                                            key={category.id}
                                            className={`px-4 py-2 rounded-full cursor-pointer transition-colors duration-200 ${
                                                selectedMiddleCategory?.id === category.id
                                                    ? 'bg-blue-100 text-blue-700'
                                                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                            }`}
                                            onClick={() => setSelectedMiddleCategory(category)}
                                        >
                                            {category.name}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                );

            case 3:
                return (
                    <div className="space-y-4">
                        <h3 className="text-lg font-medium mb-4">로드맵 정보 입력</h3>
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    로드맵 주제
                                </label>
                                <input
                                    type="text"
                                    value={formData.topic}
                                    onChange={(e) => setFormData({...formData, topic: e.target.value})}
                                    className="w-full px-3 py-2 border rounded-md"
                                    placeholder="주제를 입력하세요"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    현재 수준
                                </label>
                                <select
                                    value={formData.level}
                                    onChange={(e) => setFormData({...formData, level: e.target.value})}
                                    className="w-full px-3 py-2 border rounded-md"
                                >
                                    <option value="1">초급</option>
                                    <option value="2">중급</option>
                                    <option value="3">고급</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    학습 기간 (주)
                                </label>
                                <input
                                    type="number"
                                    min="2"
                                    max="8"
                                    value={formData.duration}
                                    onChange={(e) => {
                                        const value = parseInt(e.target.value);
                                        if (value >= 2 && value <= 8) {
                                            setFormData({...formData, duration: value});
                                        }
                                    }}
                                    className="w-full px-3 py-2 border rounded-md"
                                />
                            </div>
                        </div>
                    </div>
                );
        }
    };

    // 다음 버튼 활성화 여부 확인
    const isNextDisabled = () => {
        switch (step) {
            case 1:
                return !selectedMainCategory;
            case 2:
                return !selectedMiddleCategory;
            case 3:
                return !formData.topic.trim() || formData.duration < 1;
            default:
                return false;
        }
    };

    return (
        <div className="p-6 max-w-2xl mx-auto -mt-10">
            <h2 className="text-xl font-bold mb-4 text-gray-900">새 로드맵 샘플 만들기</h2>

            {isLoading ? (
                <div className="flex justify-center items-center h-40">
                    <p>로딩 중...</p>
                </div>
            ) : (
                <>
                    {renderStepContent()}

                    <div className="mt-6 flex justify-between">
                        <button
                            onClick={() => step > 1 ? setStep(step - 1) : hideModal()}
                            className="px-4 py-2 text-gray-600 hover:text-gray-800"
                        >
                            {step === 1 ? '취소' : '이전'}
                        </button>
                        <button
                            onClick={() => step < 3 ? setStep(step + 1) : handleSubmit()}
                            disabled={isNextDisabled()}
                            className={`px-4 py-2 rounded ${
                                isNextDisabled()
                                    ? 'bg-gray-300 cursor-not-allowed'
                                    : 'bg-blue-500 hover:bg-blue-600 text-white'
                            }`}
                        >
                            {step === 3 ? '제작' : '다음'}
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default CreateRoadmapModal;