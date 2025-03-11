import React, { useState, useEffect } from "react";
import { useModal } from "@shared/hooks/useModal";
import { studyApiService, LearningObjective } from "@core/config/studyApiConfig";
import { RoadmapRequest } from "@core/config/roadmapApiConfig";
import { getChoseong } from "es-hangul";
import SearchBox from "@pages/create-sample/SerchBox.tsx";

interface Category {
    id: number;
    name: string;
    subCategories: Category[];
}

const CreateRoadmapModal: React.FC<{ onSubmit: (request: RoadmapRequest) => void }> = ({ onSubmit }) => {
    const { hideModal } = useModal();
    const [step, setStep] = useState(1);
    const [categories, setCategories] = useState<Category[]>([]);
    const [learningObjectives, setLearningObjectives] = useState<LearningObjective[]>([]);
    const [filteredCategories, setFilteredCategories] = useState<Category[]>([]);
    const [filteredSubCategories, setFilteredSubCategories] = useState<Category[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");

    // 선택된 값들을 저장할 상태
    const [selectedMainCategory, setSelectedMainCategory] = useState<Category | null>(null);
    const [selectedMiddleCategory, setSelectedMiddleCategory] = useState<Category | null>(null);
    const [formData, setFormData] = useState({
        topic: "",
        level: "1",
        duration: 2,
        learningObjective: "" // 추가된 학습 목표 필드
    });

    // 로드맵 폼 데이터 불러오기
    useEffect(() => {
        const fetchFormData = async () => {
            setIsLoading(true);
            try {
                const response = await studyApiService.getRoadmapFormData();
                setCategories(response.categories);
                setFilteredCategories(response.categories);
                setLearningObjectives(response.learningObjectives);

                // 기본 학습 목표 설정 (첫 번째 항목)
                if (response.learningObjectives && response.learningObjectives.length > 0) {
                    setFormData(prev => ({
                        ...prev,
                        learningObjective: response.learningObjectives[0].code
                    }));
                }
            } catch (error) {
                console.error("로드맵 폼 데이터 로드 실패:", error);
                // 기존 방식으로 폴백
                try {
                    const categoriesResponse = await studyApiService.getCategoriesHierarchy();
                    setCategories(categoriesResponse);
                    setFilteredCategories(categoriesResponse);
                } catch (fallbackError) {
                    console.error("카테고리 로드 실패:", fallbackError);
                }
            }
            setIsLoading(false);
        };
        fetchFormData();
    }, []);

    // 검색어로 카테고리 필터링하는 함수 (기존 코드와 동일)
    const filterBySearchTerm = (items: Category[], searchTerm: string) => {
        // 기존 함수 내용 유지
        const normalizedQuery = searchTerm.replace(/\s+/g, "").toLowerCase();
        let queryChoseong = "";

        if (/[\u3131-\u3163\uac00-\ud7a3]/.test(normalizedQuery)) {
            queryChoseong = getChoseong(normalizedQuery);
        }

        return items.filter((item) => {
            const itemName = item.name.replace(/\s+/g, "").toLowerCase();
            let itemChoseong = "";

            if (/[\u3131-\u3163\uac00-\ud7a3]/.test(itemName)) {
                itemChoseong = getChoseong(itemName);
            }

            return (
                itemName.includes(normalizedQuery) ||
                (queryChoseong && itemChoseong.includes(queryChoseong))
            );
        });
    };

    // 대분류 검색 처리 (기존 코드와 동일)
    const handleMainCategorySearch = (query: string) => {
        setSearchQuery(query);
        setFilteredCategories(filterBySearchTerm(categories, query));
    };

    // 중분류 검색 처리 (기존 코드와 동일)
    const handleSubCategorySearch = (query: string) => {
        setSearchQuery(query);
        if (selectedMainCategory) {
            setFilteredSubCategories(filterBySearchTerm(selectedMainCategory.subCategories, query));
        }
    };

    // 대분류 선택 시 중분류 초기화 (기존 코드와 동일)
    useEffect(() => {
        if (selectedMainCategory) {
            setFilteredSubCategories(selectedMainCategory.subCategories);
            setSearchQuery("");
        }
    }, [selectedMainCategory]);

    // 최종 제출 처리 - learningObjective 필드 추가
    const handleSubmit = () => {
        if (!selectedMiddleCategory) return;

        const request: RoadmapRequest = {
            mainCategory: selectedMiddleCategory.name,
            subCategory: formData.topic,
            currentLevel: formData.level,
            duration: formData.duration,
            learningObjective: formData.learningObjective || undefined // 학습 목표 추가
        };

        onSubmit(request);
        console.log(request);
        hideModal();
    };

    // 단계별 UI 렌더링 - 3단계에 학습 목표 선택 옵션 추가
    const renderStepContent = () => {
        switch (step) {
            case 1:
                // 기존 1단계 UI 유지
                return (
                    <div className="space-y-4">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-lg font-medium">대분류 선택</h3>
                            <SearchBox
                                placeholder="대분류 검색..."
                                onSearch={handleMainCategorySearch}
                            />
                        </div>
                        <div className="flex flex-wrap gap-3">
                            <div className="bg-gray-50 p-4 rounded-lg">
                                <div className="flex flex-wrap gap-3">
                                    {filteredCategories.map((category) => (
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
                // 기존 2단계 UI 유지
                return (
                    <div className="space-y-4">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-lg font-medium">중분류 선택</h3>
                            <SearchBox
                                placeholder="중분류 검색..."
                                onSearch={handleSubCategorySearch}
                            />
                        </div>
                        <div className="flex flex-wrap gap-3">
                            <div className="bg-gray-50 p-4 rounded-lg">
                                <div className="flex flex-wrap gap-3">
                                    {(searchQuery ? filteredSubCategories : selectedMainCategory?.subCategories)?.map((category) => (
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
                // 3단계 UI에 학습 목표 선택 추가
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
                            {/* 학습 목표 선택 추가 */}
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    학습 목표
                                </label>
                                <select
                                    value={formData.learningObjective}
                                    onChange={(e) => setFormData({...formData, learningObjective: e.target.value})}
                                    className="w-full px-3 py-2 border rounded-md"
                                >
                                    {learningObjectives.map((objective) => (
                                        <option key={objective.code} value={objective.code}>
                                            {objective.description}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </div>
                );
        }
    };

    // 다음 버튼 활성화 여부 확인 - 학습 목표 필드 추가
    const isNextDisabled = () => {
        switch (step) {
            case 1:
                return !selectedMainCategory;
            case 2:
                return !selectedMiddleCategory;
            case 3:
                return !formData.topic.trim() || formData.duration < 1 || !formData.learningObjective;
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