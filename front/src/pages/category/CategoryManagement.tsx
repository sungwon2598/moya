import { useEffect, useState } from 'react';
import type { UpdateCategoryRequest, CreateCategoryRequest } from '@/types/study';
import { studyService } from '@/services/study';

interface Category {
  id: number;
  name: string;
  subCategories: Category[];
}

type CategoryType = 'main' | 'middle' | 'sub' | null;

interface CategoryHeaderProps {
  title: string;
  parentName?: string;
  showInput: boolean;
  inputValue: string;
  selectedCategory: number | null;
  selectedCategoryName?: string;
  isEditMode?: boolean;
  onInputChange: (value: string) => void;
  onToggleInput: () => void;
  onAddCategory: () => void;
  onDeleteCategory?: () => void;
  onEditCategory?: () => void;
  onSubmitEdit?: () => void;
}

const CategoryHeader = ({
  title,
  parentName,
  showInput,
  inputValue,
  selectedCategory,
  selectedCategoryName,
  isEditMode,
  onInputChange,
  onToggleInput,
  onAddCategory,
  onDeleteCategory,
  onEditCategory,
  onSubmitEdit,
}: CategoryHeaderProps) => {
  const buttonStyle =
    'px-3 py-1.5 bg-blue-500 text-white text-sm rounded-md shadow hover:bg-blue-600 transition-colors';
  const deleteButtonStyle =
    'px-3 py-1.5 bg-red-500 text-white text-sm rounded-md shadow hover:bg-red-600 transition-colors';

  const handleSubmit = () => {
    if (inputValue.trim()) {
      if (isEditMode) {
        if (confirm(`'${selectedCategoryName}'를 '${inputValue}'로 수정하시겠습니까?`)) {
          onSubmitEdit?.();
        }
      } else {
        if (confirm(`'${inputValue}' 카테고리를 추가하시겠습니까?`)) {
          onAddCategory();
        }
      }
    }
  };

  const handleButtonClick = () => {
    if (!showInput) {
      if (isEditMode) {
        onEditCategory?.();
      } else {
        onToggleInput();
      }
    } else if (inputValue.trim()) {
      handleSubmit();
    } else {
      onToggleInput();
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && inputValue.trim()) {
      handleSubmit();
    }
  };

  const getButtonText = () => {
    if (!showInput) {
      return isEditMode ? '수정' : '추가';
    }
    if (inputValue.trim()) {
      return isEditMode ? '완료' : '추가';
    }
    return '취소';
  };

  return (
    <div className="mb-4 flex items-center justify-between">
      <div className="flex flex-1 items-center gap-3">
        <div className="flex items-center gap-2">
          <h2 className="text-lg font-semibold">{title}</h2>
          {parentName && <span className="text-gray-500">- {parentName}</span>}
        </div>
        {showInput && (
          <input
            type="text"
            value={inputValue}
            onChange={(e) => onInputChange(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="카테고리명 입력"
            className="max-w-xs flex-1 rounded-md border px-3 py-1.5"
            autoFocus
          />
        )}
      </div>
      <div className="flex gap-2">
        {selectedCategory && !isEditMode && !showInput && (
          <>
            <button
              onClick={onEditCategory}
              className="rounded-md bg-yellow-500 px-3 py-1.5 text-sm text-white shadow transition-colors hover:bg-yellow-600">
              수정
            </button>
            {onDeleteCategory && (
              <button onClick={onDeleteCategory} className={deleteButtonStyle}>
                삭제
              </button>
            )}
          </>
        )}
        <button onClick={handleButtonClick} className={buttonStyle}>
          {getButtonText()}
        </button>
      </div>
    </div>
  );
};

const CategoryManagement = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedMainCategory, setSelectedMainCategory] = useState<number | null>(null);
  const [selectedSubCategory, setSelectedSubCategory] = useState<number | null>(null);
  const [selectedMinorCategory, setSelectedMinorCategory] = useState<number | null>(null);
  const [activeInput, setActiveInput] = useState<CategoryType>(null);
  const [inputValue, setInputValue] = useState('');
  const [isEditMode, setIsEditMode] = useState(false);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const response = await studyService.getCategoriesHierarchy();
        setCategories(response);
      } catch (error) {
        console.error('카테고리 데이터를 불러오는데 실패했습니다:', error);
        setError('데이터를 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchCategories();
  }, []);

  const toggleInput = (type: CategoryType) => {
    if (activeInput === type) {
      setActiveInput(null);
      setInputValue('');
      setIsEditMode(false);
    } else {
      setActiveInput(type);
      setInputValue('');
      setIsEditMode(false);
    }
  };

  const handleAddCategory = async () => {
    try {
      const createRequest: CreateCategoryRequest = {
        name: inputValue,
      };

      if (activeInput === 'middle' && selectedMainCategory) {
        createRequest.parentId = selectedMainCategory;
      } else if (activeInput === 'sub' && selectedSubCategory) {
        createRequest.parentId = selectedSubCategory;
      }

      await studyService.createCategory(createRequest);

      // 카테고리 목록 새로고침
      const response = await studyService.getCategoriesHierarchy();
      setCategories(response);

      // 입력 필드 초기화
      setInputValue('');
      setActiveInput(null);
    } catch (error) {
      console.error('카테고리 추가에 실패했습니다:', error);
      alert('카테고리 추가에 실패했습니다.');
    }
  };

  const handleDeleteCategory = async (type: CategoryType, minorCategoryId?: number) => {
    try {
      let categoryId: number | null = null;

      if (type === 'main') {
        categoryId = selectedMainCategory;
      } else if (type === 'middle') {
        categoryId = selectedSubCategory;
      } else if (type === 'sub' && minorCategoryId) {
        categoryId = minorCategoryId;
      }

      if (!categoryId) return;

      await studyService.deleteCategory(categoryId);

      // 삭제 후 선택 상태 초기화
      if (type === 'main') {
        setSelectedMainCategory(null);
        setSelectedSubCategory(null);
        setSelectedMinorCategory(null);
      } else if (type === 'middle') {
        setSelectedSubCategory(null);
        setSelectedMinorCategory(null);
      } else if (type === 'sub') {
        setSelectedMinorCategory(null);
      }

      // 카테고리 목록 새로고침
      const response = await studyService.getCategoriesHierarchy();
      setCategories(response);

      setActiveInput(null);
    } catch (error) {
      console.error('카테고리 삭제에 실패했습니다:', error);
      alert('카테고리 삭제에 실패했습니다.');
    }
  };

  const handleEditCategory = (type: CategoryType) => {
    setIsEditMode(true);
    setActiveInput(type);

    if (type === 'main') {
      setInputValue(getSelectedMainCategory()?.name || '');
    } else if (type === 'middle') {
      setInputValue(getSelectedSubCategory()?.name || '');
    } else if (type === 'sub') {
      const minorCategory = getSelectedSubCategorySubCategories().find((cat) => cat.id === selectedMinorCategory);
      setInputValue(minorCategory?.name || '');
    }
  };

  const handleSubmitEdit = async (type: CategoryType) => {
    try {
      let categoryId: number | null = null;

      if (type === 'main') {
        categoryId = selectedMainCategory;
      } else if (type === 'middle') {
        categoryId = selectedSubCategory;
      } else if (type === 'sub') {
        categoryId = selectedMinorCategory;
      }

      if (!categoryId) return;

      const updateRequest: UpdateCategoryRequest = {
        name: inputValue,
      };

      await studyService.updateCategory(categoryId, updateRequest);

      // 카테고리 목록 새로고침
      const response = await studyService.getCategoriesHierarchy();
      setCategories(response);

      // 입력 필드 초기화
      setInputValue('');
      setActiveInput(null);
      setIsEditMode(false);
    } catch (error) {
      console.error('카테고리 수정에 실패했습니다:', error);
      alert('카테고리 수정에 실패했습니다.');
    }
  };

  if (isLoading) {
    return (
      <div className="mx-auto max-w-4xl p-6">
        <div className="flex h-40 items-center justify-center">
          <div className="text-gray-500">로딩 중...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="mx-auto max-w-4xl p-6">
        <div className="rounded-lg bg-red-50 p-4 text-red-500">{error}</div>
      </div>
    );
  }

  const getSelectedMainCategory = () => {
    return categories.find((cat) => cat.id === selectedMainCategory);
  };

  const getSelectedSubCategory = () => {
    const mainCategory = categories.find((cat) => cat.id === selectedMainCategory);
    return mainCategory?.subCategories.find((cat) => cat.id === selectedSubCategory);
  };

  const getSelectedMainCategorySubCategories = () => {
    return getSelectedMainCategory()?.subCategories || [];
  };

  const getSelectedSubCategorySubCategories = () => {
    return getSelectedSubCategory()?.subCategories || [];
  };

  const EmptyMessage = () => (
    <div className="flex h-20 w-full items-center justify-center text-gray-500">카테고리가 없습니다.</div>
  );

  return (
    <div className="mx-auto max-w-4xl space-y-6 p-6">
      <h1 className="mb-6 text-2xl font-bold">카테고리 관리</h1>

      {/* 대분류 섹션 */}
      <section className="relative rounded-lg bg-white p-6 shadow">
        <CategoryHeader
          title="대분류"
          showInput={activeInput === 'main'}
          inputValue={inputValue}
          selectedCategory={selectedMainCategory}
          selectedCategoryName={getSelectedMainCategory()?.name}
          isEditMode={isEditMode && activeInput === 'main'}
          onInputChange={setInputValue}
          onToggleInput={() => toggleInput('main')}
          onAddCategory={handleAddCategory}
          onDeleteCategory={
            selectedMainCategory && getSelectedMainCategorySubCategories().length === 0
              ? () => handleDeleteCategory('main')
              : undefined
          }
          onEditCategory={() => handleEditCategory('main')}
          onSubmitEdit={() => handleSubmitEdit('main')}
        />
        <div className="flex flex-wrap gap-3">
          {categories.length > 0 ? (
            categories.map((category) => (
              <div
                key={category.id}
                className={`cursor-pointer rounded-full px-4 py-2 transition-colors duration-200 ${
                  selectedMainCategory === category.id
                    ? 'bg-blue-100 text-blue-700'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
                onClick={() => {
                  setSelectedMainCategory(selectedMainCategory === category.id ? null : category.id);
                  setSelectedSubCategory(null);
                  setSelectedMinorCategory(null);
                  setActiveInput(null);
                  setIsEditMode(false);
                }}>
                {category.name}
              </div>
            ))
          ) : (
            <EmptyMessage />
          )}
        </div>
      </section>

      {/* 중분류 섹션 */}
      {selectedMainCategory && (
        <section className="relative animate-[fadeIn_0.3s_ease-in-out] rounded-lg bg-white p-6 shadow">
          <CategoryHeader
            title="중분류"
            parentName={getSelectedMainCategory()?.name}
            showInput={activeInput === 'middle'}
            inputValue={inputValue}
            selectedCategory={selectedSubCategory}
            selectedCategoryName={getSelectedSubCategory()?.name}
            isEditMode={isEditMode && activeInput === 'middle'}
            onInputChange={setInputValue}
            onToggleInput={() => toggleInput('middle')}
            onAddCategory={handleAddCategory}
            onDeleteCategory={
              selectedSubCategory && getSelectedSubCategorySubCategories().length === 0
                ? () => handleDeleteCategory('middle')
                : undefined
            }
            onEditCategory={() => handleEditCategory('middle')}
            onSubmitEdit={() => handleSubmitEdit('middle')}
          />
          <div className="flex flex-wrap gap-3">
            {getSelectedMainCategorySubCategories().length > 0 ? (
              getSelectedMainCategorySubCategories().map((subCategory) => (
                <div
                  key={subCategory.id}
                  className={`cursor-pointer rounded-full px-3 py-1.5 transition-colors duration-200 ${
                    selectedSubCategory === subCategory.id
                      ? 'bg-blue-50 text-blue-600'
                      : 'bg-gray-50 text-gray-600 hover:bg-gray-100'
                  }`}
                  onClick={() => {
                    setSelectedSubCategory(selectedSubCategory === subCategory.id ? null : subCategory.id);
                    setSelectedMinorCategory(null);
                    setActiveInput(null);
                    setIsEditMode(false);
                  }}>
                  {subCategory.name}
                </div>
              ))
            ) : (
              <EmptyMessage />
            )}
          </div>
        </section>
      )}

      {/* 소분류 섹션 */}
      {selectedSubCategory && (
        <section className="relative animate-[fadeIn_0.3s_ease-in-out] rounded-lg bg-white p-6 shadow">
          <CategoryHeader
            title="소분류"
            parentName={getSelectedSubCategory()?.name}
            showInput={activeInput === 'sub'}
            inputValue={inputValue}
            selectedCategory={selectedMinorCategory}
            selectedCategoryName={
              getSelectedSubCategorySubCategories().find((cat) => cat.id === selectedMinorCategory)?.name
            }
            isEditMode={isEditMode && activeInput === 'sub'}
            onInputChange={setInputValue}
            onToggleInput={() => toggleInput('sub')}
            onAddCategory={handleAddCategory}
            onDeleteCategory={
              selectedMinorCategory ? () => handleDeleteCategory('sub', selectedMinorCategory) : undefined
            }
            onEditCategory={() => handleEditCategory('sub')}
            onSubmitEdit={() => handleSubmitEdit('sub')}
          />
          <div className="flex flex-wrap gap-3">
            {getSelectedSubCategorySubCategories().length > 0 ? (
              getSelectedSubCategorySubCategories().map((minorCategory) => (
                <div
                  key={minorCategory.id}
                  className={`cursor-pointer rounded-full px-3 py-1.5 transition-colors duration-200 ${
                    selectedMinorCategory === minorCategory.id
                      ? 'bg-blue-50 text-blue-600'
                      : 'bg-gray-50 text-gray-600 hover:bg-gray-100'
                  }`}
                  onClick={() => {
                    setSelectedMinorCategory(selectedMinorCategory === minorCategory.id ? null : minorCategory.id);
                    setActiveInput(null);
                    setIsEditMode(false);
                  }}>
                  {minorCategory.name}
                </div>
              ))
            ) : (
              <EmptyMessage />
            )}
          </div>
        </section>
      )}
    </div>
  );
};

export default CategoryManagement;
