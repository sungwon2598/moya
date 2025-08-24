import React, { useState, useEffect } from 'react';
import { useModal } from '@shared/hooks/useModal';
import { studyService } from '@/services/study';
import { RoadmapRequest } from '@core/config/roadmapApiConfig';
import { getChoseong } from 'es-hangul';
import SearchBox from '@pages/create-sample/SerchBox.tsx';

interface Category {
  id: number;
  name: string;
  subCategories: Category[];
}

const CreateRoadmapModal: React.FC<{ onSubmit: (request: RoadmapRequest) => void }> = ({ onSubmit }) => {
  const { hideModal } = useModal();
  const [step, setStep] = useState(1);
  const [categories, setCategories] = useState<Category[]>([]);
  const [filteredCategories, setFilteredCategories] = useState<Category[]>([]);
  const [filteredSubCategories, setFilteredSubCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // 선택된 값들을 저장할 상태
  const [selectedMainCategory, setSelectedMainCategory] = useState<Category | null>(null);
  const [selectedMiddleCategory, setSelectedMiddleCategory] = useState<Category | null>(null);
  const [formData, setFormData] = useState({
    topic: '',
    level: '1',
    duration: 2,
  });

  // 카테고리 데이터 불러오기
  useEffect(() => {
    const fetchCategories = async () => {
      setIsLoading(true);
      try {
        const response = await studyService.getCategoriesHierarchy();
        setCategories(response);
        setFilteredCategories(response);
      } catch (error) {
        console.error('카테고리 로드 실패:', error);
      }
      setIsLoading(false);
    };
    fetchCategories();
  }, []);

  // 검색어로 카테고리 필터링하는 함수
  const filterBySearchTerm = (items: Category[], searchTerm: string) => {
    const normalizedQuery = searchTerm.replace(/\s+/g, '').toLowerCase();
    let queryChoseong = '';

    if (/[\u3131-\u3163\uac00-\ud7a3]/.test(normalizedQuery)) {
      queryChoseong = getChoseong(normalizedQuery);
    }

    return items.filter((item) => {
      const itemName = item.name.replace(/\s+/g, '').toLowerCase();
      let itemChoseong = '';

      if (/[\u3131-\u3163\uac00-\ud7a3]/.test(itemName)) {
        itemChoseong = getChoseong(itemName);
      }

      return itemName.includes(normalizedQuery) || (queryChoseong && itemChoseong.includes(queryChoseong));
    });
  };

  // 대분류 검색 처리
  const handleMainCategorySearch = (query: string) => {
    setSearchQuery(query);
    setFilteredCategories(filterBySearchTerm(categories, query));
  };

  // 중분류 검색 처리
  const handleSubCategorySearch = (query: string) => {
    setSearchQuery(query);
    if (selectedMainCategory) {
      setFilteredSubCategories(filterBySearchTerm(selectedMainCategory.subCategories, query));
    }
  };

  // 대분류 선택 시 중분류 초기화
  useEffect(() => {
    if (selectedMainCategory) {
      setFilteredSubCategories(selectedMainCategory.subCategories);
      setSearchQuery('');
    }
  }, [selectedMainCategory]);

  // 최종 제출 처리
  const handleSubmit = () => {
    if (!selectedMiddleCategory) return;

    const request: RoadmapRequest = {
      mainCategory: selectedMiddleCategory.name,
      subCategory: formData.topic,
      currentLevel: formData.level,
      duration: formData.duration,
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
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-lg font-medium">대분류 선택</h3>
              <SearchBox placeholder="대분류 검색..." onSearch={handleMainCategorySearch} />
            </div>
            <div className="flex flex-wrap gap-3">
              <div className="rounded-lg bg-gray-50 p-4">
                <div className="flex flex-wrap gap-3">
                  {filteredCategories.map((category) => (
                    <div
                      key={category.id}
                      className={`cursor-pointer rounded-full px-4 py-2 transition-colors duration-200 ${
                        selectedMainCategory?.id === category.id
                          ? 'bg-blue-100 text-blue-700'
                          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                      }`}
                      onClick={() => setSelectedMainCategory(category)}>
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
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-lg font-medium">중분류 선택</h3>
              <SearchBox placeholder="중분류 검색..." onSearch={handleSubCategorySearch} />
            </div>
            <div className="flex flex-wrap gap-3">
              <div className="rounded-lg bg-gray-50 p-4">
                <div className="flex flex-wrap gap-3">
                  {(searchQuery ? filteredSubCategories : selectedMainCategory?.subCategories)?.map((category) => (
                    <div
                      key={category.id}
                      className={`cursor-pointer rounded-full px-4 py-2 transition-colors duration-200 ${
                        selectedMiddleCategory?.id === category.id
                          ? 'bg-blue-100 text-blue-700'
                          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                      }`}
                      onClick={() => setSelectedMiddleCategory(category)}>
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
            <h3 className="mb-4 text-lg font-medium">로드맵 정보 입력</h3>
            <div className="space-y-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">로드맵 주제</label>
                <input
                  type="text"
                  value={formData.topic}
                  onChange={(e) => setFormData({ ...formData, topic: e.target.value })}
                  className="w-full rounded-md border px-3 py-2"
                  placeholder="주제를 입력하세요"
                />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">현재 수준</label>
                <select
                  value={formData.level}
                  onChange={(e) => setFormData({ ...formData, level: e.target.value })}
                  className="w-full rounded-md border px-3 py-2">
                  <option value="1">초급</option>
                  <option value="2">중급</option>
                  <option value="3">고급</option>
                </select>
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">학습 기간 (주)</label>
                <input
                  type="number"
                  min="2"
                  max="8"
                  value={formData.duration}
                  onChange={(e) => {
                    const value = parseInt(e.target.value);
                    if (value >= 2 && value <= 8) {
                      setFormData({ ...formData, duration: value });
                    }
                  }}
                  className="w-full rounded-md border px-3 py-2"
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
    <div className="mx-auto -mt-10 max-w-2xl p-6">
      <h2 className="mb-4 text-xl font-bold text-gray-900">새 로드맵 샘플 만들기</h2>

      {isLoading ? (
        <div className="flex h-40 items-center justify-center">
          <p>로딩 중...</p>
        </div>
      ) : (
        <>
          {renderStepContent()}

          <div className="mt-6 flex justify-between">
            <button
              onClick={() => (step > 1 ? setStep(step - 1) : hideModal())}
              className="px-4 py-2 text-gray-600 hover:text-gray-800">
              {step === 1 ? '취소' : '이전'}
            </button>
            <button
              onClick={() => (step < 3 ? setStep(step + 1) : handleSubmit())}
              disabled={isNextDisabled()}
              className={`rounded px-4 py-2 ${
                isNextDisabled() ? 'cursor-not-allowed bg-gray-300' : 'bg-blue-500 text-white hover:bg-blue-600'
              }`}>
              {step === 3 ? '제작' : '다음'}
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default CreateRoadmapModal;
