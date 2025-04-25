import React, { useState } from 'react';
import { Input } from '@/components/shared/Input';
import { Alert } from '@/components/shared/Alert';
import { Card } from '../../../shared/ui';
import {
  ProfileFormData,
  ProfileFormProps,
  POSITION_OPTIONS,
  EXPERIENCE_OPTIONS,
  ValidationErrors,
  VALIDATION_MESSAGES,
} from '@/core/types/profile';

const POSITIONS = [
  { value: POSITION_OPTIONS.EMPTY, label: '직무 선택' },
  { value: POSITION_OPTIONS.FRONTEND, label: '프론트엔드' },
  { value: POSITION_OPTIONS.BACKEND, label: '백엔드' },
  { value: POSITION_OPTIONS.FULLSTACK, label: '풀스택' },
  { value: POSITION_OPTIONS.DEVOPS, label: '데브옵스' },
  { value: POSITION_OPTIONS.MOBILE, label: '모바일' },
] as const;

const EXPERIENCES = [
  { value: EXPERIENCE_OPTIONS.EMPTY, label: '경력 선택' },
  { value: EXPERIENCE_OPTIONS.ZERO, label: '신입' },
  { value: EXPERIENCE_OPTIONS.ONE, label: '1년' },
  { value: EXPERIENCE_OPTIONS.TWO, label: '2년' },
  { value: EXPERIENCE_OPTIONS.THREE, label: '3년' },
  { value: EXPERIENCE_OPTIONS.FOUR, label: '4년' },
  { value: EXPERIENCE_OPTIONS.FIVE_PLUS, label: '5년 이상' },
] as const;

const ProfileForm: React.FC<ProfileFormProps> = ({ initialData = {}, onSubmit }) => {
  const [formData, setFormData] = useState<ProfileFormData>({
    nickname: '',
    position: POSITION_OPTIONS.EMPTY,
    school: '',
    experience: EXPERIENCE_OPTIONS.EMPTY,
    description: '',
    links: [''],
    ...initialData,
  });

  const [errors, setErrors] = useState<ValidationErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = (): boolean => {
    const newErrors: ValidationErrors = {};

    if (!formData.nickname.trim()) {
      newErrors.nickname = VALIDATION_MESSAGES.NICKNAME_REQUIRED;
    }

    if (formData.position === POSITION_OPTIONS.EMPTY) {
      newErrors.position = VALIDATION_MESSAGES.POSITION_REQUIRED;
    }

    if (formData.experience === EXPERIENCE_OPTIONS.EMPTY) {
      newErrors.experience = VALIDATION_MESSAGES.EXPERIENCE_REQUIRED;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);
    try {
      await onSubmit(formData);
    } catch (error) {
      console.log(error);
      setErrors((prev) => ({
        ...prev,
        submit: VALIDATION_MESSAGES.SUBMIT_ERROR,
      }));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (errors[name as keyof ProfileFormData]) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[name as keyof ProfileFormData];
        return newErrors;
      });
    }
  };

  const handleLinkChange = (index: number, value: string) => {
    const newLinks = [...formData.links];
    newLinks[index] = value;
    setFormData((prev) => ({
      ...prev,
      links: newLinks,
    }));
  };

  const addLink = () => {
    setFormData((prev) => ({
      ...prev,
      links: [...prev.links, ''],
    }));
  };

  const removeLink = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      links: prev.links.filter((_, i) => i !== index),
    }));
  };

  return (
    <Card.Card className="w-xl mt-10 rounded-2xl border-0 bg-white px-4 py-6 shadow-sm">
      <form onSubmit={handleSubmit} className="space-y-6">
        {errors.submit && <Alert variant="error">{errors.submit}</Alert>}

        <div>
          <label className="block text-sm font-medium text-gray-700">닉네임 *</label>
          <Input
            type="text"
            name="nickname"
            value={formData.nickname}
            onChange={handleInputChange}
            placeholder="닉네임을 입력하세요"
            required
            maxLength={20}
            error={errors.nickname}
          />
          {errors.nickname && <p className="mt-1 text-sm text-red-500">{errors.nickname}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">직무 *</label>
          <select
            name="position"
            value={formData.position}
            onChange={handleInputChange}
            className={`focus:border-moya-primary focus:ring-moya-primary mt-1 block w-full rounded-md border-gray-300 shadow-sm ${
              errors.position ? 'border-red-500' : ''
            }`}
            required>
            {POSITIONS.map(({ value, label }) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
          {errors.position && <p className="mt-1 text-sm text-red-500">{errors.position}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">학교</label>
          <Input
            type="text"
            name="school"
            value={formData.school}
            onChange={handleInputChange}
            placeholder="학교명을 입력하세요"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">경력 *</label>
          <select
            name="experience"
            value={formData.experience}
            onChange={handleInputChange}
            className={`focus:border-moya-primary focus:ring-moya-primary mt-1 block w-full rounded-md border-gray-300 shadow-sm ${
              errors.experience ? 'border-red-500' : ''
            }`}
            required>
            {EXPERIENCES.map(({ value, label }) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
          {errors.experience && <p className="mt-1 text-sm text-red-500">{errors.experience}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">자기소개</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleInputChange}
            rows={4}
            className={`focus:border-moya-primary focus:ring-moya-primary mt-1 block w-full rounded-md border-gray-300 shadow-sm ${
              errors.description ? 'border-red-500' : ''
            }`}
            placeholder="자기소개를 입력하세요"
          />
          {errors.description && <p className="mt-1 text-sm text-red-500">{errors.description}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">외부 링크</label>
          <div className="space-y-2">
            {formData.links.map((link, index) => (
              <div key={index} className="flex gap-2">
                <Input
                  type="url"
                  value={link}
                  onChange={(e) => handleLinkChange(index, e.target.value)}
                  placeholder="https://"
                  className="flex-1"
                />
                {formData.links.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeLink(index)}
                    className="px-3 py-2 text-red-600 hover:text-red-700">
                    삭제
                  </button>
                )}
              </div>
            ))}
            <button type="button" onClick={addLink} className="text-moya-primary hover:text-moya-secondary text-sm">
              + 링크 추가
            </button>
          </div>
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="bg-moya-primary hover:bg-moya-secondary w-full rounded-lg px-4 py-2 text-white transition-colors duration-200 disabled:opacity-50">
          {isSubmitting ? '저장 중...' : '저장'}
        </button>
      </form>
    </Card.Card>
  );
};

export { ProfileForm };
