import React, { useState, useEffect } from 'react';
import { User, School, Code } from 'lucide-react';

// Constants
const POSITIONS = ['프론트엔드', '백엔드', '풀스택', '데브옵스', '모바일'] as const;
const YEARS = ['0년', '1년', '2년', '3년', '4년', '5년 이상'] as const;

type Position = typeof POSITIONS[number];
type ExperienceYear = typeof YEARS[number];

interface ProfileFormData {
    nickname: string;
    position: Position | '';
    school: string;
    experience: ExperienceYear | '';
    description: string;
    links: string[];
}

interface ProfileFormProps {
    onSubmitSuccess?: () => void;
}

export default function ProfileForm({ onSubmitSuccess }: ProfileFormProps) {
    const [formData, setFormData] = useState<ProfileFormData>({
        nickname: '',
        position: '',
        school: '',
        experience: '',
        description: '',
        links: ['']
    });

    const [errors, setErrors] = useState<Partial<ProfileFormData & { submit: string }>>({});
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    useEffect(() => {
        const fetchProfileData = async () => {
            try {
                // 임시로 fetch 대신 setTimeout 사용
                setTimeout(() => {
                    setFormData({
                        nickname: '테스트 사용자',
                        position: '프론트엔드',
                        school: '테스트 학교',
                        experience: '1년',
                        description: '안녕하세요',
                        links: ['https://github.com']
                    });
                    setIsLoading(false);
                }, 1000);
            } catch (error) {
                console.error('프로필 데이터 로딩 실패:', error);
                setIsLoading(false);
            }
        };

        fetchProfileData();
    }, []);

    const validateForm = () => {
        const newErrors: Partial<ProfileFormData> = {};
        if (!formData.nickname.trim()) {
            newErrors.nickname = '닉네임을 입력해주세요';
        }
        if (!formData.position) {
            newErrors.position = '직무를 선택해주세요';
        }
        if (!formData.experience) {
            newErrors.experience = '경력을 선택해주세요';
        }
        if (!formData.description.trim()) {
            newErrors.description = '자기소개를 입력해주세요';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!validateForm()) return;

        try {
            setIsSubmitting(true);
            setSuccessMessage(null);
            // 임시로 API 호출 대신 setTimeout 사용
            setTimeout(() => {
                setSuccessMessage('프로필이 성공적으로 수정되었습니다.');
                onSubmitSuccess?.();
                setIsSubmitting(false);
                setTimeout(() => setSuccessMessage(null), 3000);
            }, 1000);
        } catch (error) {
            console.error('프로필 수정 실패:', error);
            setErrors(prev => ({ ...prev, submit: '프로필 수정 중 오류가 발생했습니다' }));
            setIsSubmitting(false);
        }
    };

    const handleLinkChange = (index: number, value: string) => {
        const newLinks = [...formData.links];
        newLinks[index] = value;
        setFormData({ ...formData, links: newLinks });
    };

    const addLink = () => {
        setFormData(prev => ({ ...prev, links: [...prev.links, ''] }));
    };

    const removeLink = (index: number) => {
        setFormData(prev => ({
            ...prev,
            links: prev.links.filter((_, i) => i !== index)
        }));
    };

    if (isLoading) {
        return (
            <div className="flex justify-center items-center h-[400px]">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-moya-primary"></div>
            </div>
        );
    }

    return (
        <div className="w-full max-w-2xl mx-auto p-6">
            <div className="text-center mb-8">
                <h2 className="text-3xl font-bold text-gray-900">프로필 수정</h2>
                <p className="mt-2 text-sm text-gray-600">
                    내 정보를 수정하고 관리할 수 있습니다
                </p>
            </div>

            {successMessage && (
                <div className="mb-4 p-4 bg-green-50 text-green-700 rounded-lg">
                    {successMessage}
                </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-6 bg-white p-8 rounded-lg shadow">
                {/* 닉네임 입력 */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        닉네임 <span className="text-red-500">*</span>
                    </label>
                    <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <User className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                            type="text"
                            value={formData.nickname}
                            onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
                            className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-moya-primary focus:border-moya-primary"
                            placeholder="닉네임을 입력하세요"
                        />
                    </div>
                    {errors.nickname && (
                        <p className="mt-1 text-sm text-red-600">{errors.nickname}</p>
                    )}
                </div>

                {/* 직무 선택 */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        직무 <span className="text-red-500">*</span>
                    </label>
                    <select
                        value={formData.position}
                        onChange={(e) => setFormData({ ...formData, position: e.target.value as Position })}
                        className="block w-full pl-3 pr-10 py-2 border border-gray-300 rounded-md focus:ring-moya-primary focus:border-moya-primary"
                    >
                        <option value="">직무를 선택하세요</option>
                        {POSITIONS.map((position) => (
                            <option key={position} value={position}>
                                {position}
                            </option>
                        ))}
                    </select>
                    {errors.position && (
                        <p className="mt-1 text-sm text-red-600">{errors.position}</p>
                    )}
                </div>

                {/* 학교 입력 */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        학교
                    </label>
                    <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <School className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                            type="text"
                            value={formData.school}
                            onChange={(e) => setFormData({ ...formData, school: e.target.value })}
                            className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-moya-primary focus:border-moya-primary"
                            placeholder="학교를 입력하세요"
                        />
                    </div>
                </div>

                {/* 경력 선택 */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        경력 <span className="text-red-500">*</span>
                    </label>
                    <select
                        value={formData.experience}
                        onChange={(e) => setFormData({ ...formData, experience: e.target.value as ExperienceYear })}
                        className="block w-full pl-3 pr-10 py-2 border border-gray-300 rounded-md focus:ring-moya-primary focus:border-moya-primary"
                    >
                        <option value="">경력을 선택하세요</option>
                        {YEARS.map((year) => (
                            <option key={year} value={year}>
                                {year}
                            </option>
                        ))}
                    </select>
                    {errors.experience && (
                        <p className="mt-1 text-sm text-red-600">{errors.experience}</p>
                    )}
                </div>

                {/* 자기소개 */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        자기소개 <span className="text-red-500">*</span>
                    </label>
                    <textarea
                        value={formData.description}
                        onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                        rows={4}
                        className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-moya-primary focus:border-moya-primary"
                        placeholder="자기소개를 입력하세요"
                    />
                    {errors.description && (
                        <p className="mt-1 text-sm text-red-600">{errors.description}</p>
                    )}
                </div>

                {/* 링크 추가 */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        링크
                    </label>
                    <div className="space-y-2">
                        {formData.links.map((link, index) => (
                            <div key={index} className="flex gap-2">
                                <div className="relative flex-1">
                                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                        <Code className="h-5 w-5 text-gray-400" />
                                    </div>
                                    <input
                                        type="url"
                                        value={link}
                                        onChange={(e) => handleLinkChange(index, e.target.value)}
                                        className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-moya-primary focus:border-moya-primary"
                                        placeholder="https://"
                                    />
                                </div>
                                {index > 0 && (
                                    <button
                                        type="button"
                                        onClick={() => removeLink(index)}
                                        className="px-3 py-2 bg-red-100 text-red-600 rounded-md hover:bg-red-200"
                                    >
                                        삭제
                                    </button>
                                )}
                            </div>
                        ))}
                        <button
                            type="button"
                            onClick={addLink}
                            className="text-sm text-moya-primary hover:text-moya-secondary"
                        >
                            + 링크 추가
                        </button>
                    </div>
                </div>

                {/* 제출 버튼 */}
                <div>
                    <button
                        type="submit"
                        disabled={isSubmitting}
                        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-moya-primary hover:bg-moya-secondary focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-moya-primary disabled:opacity-50"
                    >
                        {isSubmitting ? '저장 중...' : '프로필 저장'}
                    </button>
                    {errors.submit && (
                        <p className="mt-2 text-sm text-red-600 text-center">{errors.submit}</p>
                    )}
                </div>
            </form>
        </div>
    );
}
