import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Category, CreateStudyDTO, studyApiService } from '@/core/config/studyApiConfig';
import Dropdown from './Dropdown';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

const StudyPostCreate = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState<CreateStudyDTO>({
    title: '',
    content: '',
    recruits: 0,
    expectedPeriod: '',
    startDate: '',
    endDate: '',
    studies: [],
    studyDetails: [],
  });

  const recruitOptions = [
    { value: '1', label: '1명' },
    { value: '2', label: '2명' },
    { value: '3', label: '3명' },
    { value: '4', label: '4명' },
    { value: '5', label: '5명' },
    { value: '6', label: '6명 이상' },
  ];

  const periodOptions = [
    { value: '1개월', label: '1개월' },
    { value: '2개월', label: '2개월' },
    { value: '3개월', label: '3개월' },
    { value: '6개월', label: '6개월' },
    { value: '6개월 이상', label: '6개월 이상' },
  ];

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await studyApiService.getCategoriesHierarchy();
        setCategories(response);
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };

    fetchCategories();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      // Format the data according to the API specification
      const postData: CreateStudyDTO = {
        title: formData.title,
        content: formData.content,
        recruits: parseInt(formData.recruits.toString()),
        expectedPeriod: formData.expectedPeriod,
        studies: formData.studies,
        studyDetails: formData.studyDetails,
        startDate: new Date(formData.startDate).toISOString(),
        endDate: new Date(formData.endDate).toISOString(),
      };
      await studyApiService.createPost(postData);
      navigate('/study');
    } catch (error) {
      console.error('Failed to submit post:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof CreateStudyDTO, value: any) => {
    if (field === 'studies') {
      // Ensure studies is always treated as an array
      setFormData((prev) => ({
        ...prev,
        [field]: [value], // Wrap the value in an array
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [field]: value,
      }));
    }
    //  setFormData(prev => ({
    //         ...prev,
    //         [field]: value
    //     }));
  };

  const modules = {
    toolbar: [
      [{ header: [1, 2, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ list: 'ordered' }, { list: 'bullet' }],
      ['link', 'image'],
      ['clean'],
    ],
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-12 w-12 animate-spin rounded-full border-4 border-blue-500 border-t-transparent" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="mx-auto max-w-3xl px-6">
        <div className="rounded-3xl border border-gray-200 bg-white p-8 shadow-sm">
          <h1 className="mb-8 text-2xl font-bold">프로젝트 기본 정보를 입력해주세요</h1>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Category Selection */}
            <div className="grid grid-cols-2 gap-4">
              <Dropdown
                label="모집 구분"
                categories={categories}
                onChange={(value) => handleChange('studies', value)}
                value={Array.isArray(formData.studies) && formData.studies.length > 0 ? formData.studies[0] : ''}
                // value={formData.studies}
                isCategory={true}
              />
              <Dropdown
                label="모집 인원"
                options={recruitOptions}
                onChange={(value) => handleChange('recruits', parseInt(value))}
                value={formData.recruits.toString()}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <Dropdown
                label="진행 기간"
                options={periodOptions}
                onChange={(value) => handleChange('expectedPeriod', value)}
                value={formData.expectedPeriod}
              />
            </div>

            {/* Date Selection */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">모집 시작일</label>
                <input
                  type="date"
                  value={formData.startDate}
                  onChange={(e) => handleChange('startDate', e.target.value)}
                  className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-transparent focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">모집 마감일</label>
                <input
                  type="date"
                  value={formData.endDate}
                  onChange={(e) => handleChange('endDate', e.target.value)}
                  className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-transparent focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            {/* Title */}
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">제목</label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) => handleChange('title', e.target.value)}
                placeholder="글 제목을 입력해주세요"
                className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:border-transparent focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Content */}
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">프로젝트 소개</label>
              <ReactQuill
                theme="snow"
                value={formData.content}
                onChange={(content) => handleChange('content', content)}
                modules={modules}
                className="h-64 bg-white"
              />
            </div>

            {/* Submit Buttons */}
            <div className="flex justify-end gap-4 pt-6">
              <button
                type="button"
                onClick={() => navigate('/study')}
                className="rounded-xl border border-gray-300 bg-white px-6 py-3 text-gray-700 transition-colors hover:bg-gray-50">
                취소
              </button>
              <button
                type="submit"
                className="rounded-xl bg-blue-500 px-6 py-3 text-white transition-colors hover:bg-blue-600"
                disabled={loading}>
                글쓰기
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default StudyPostCreate;
