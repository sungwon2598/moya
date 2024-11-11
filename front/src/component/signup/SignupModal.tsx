import React, { useState } from 'react';
import { useModal } from '../../hooks/useModal';
import { X } from 'lucide-react';
import {SignupFormData} from "../../types/signup.ts";

export const SignupModal: React.FC = () => {
    const { hideModal } = useModal();
    const [formData, setFormData] = useState<SignupFormData>({
        nickname: '',
        password: '',
        termsAgreed: false
    });

    // 비밀번호 유효성 검사
    const validatePassword = (password: string): boolean => {
        const hasUpperCase = /[A-Z]/.test(password);
        const hasLowerCase = /[a-z]/.test(password);
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
        const isLongEnough = password.length >= 8;

        return hasUpperCase && hasLowerCase && hasSpecialChar && isLongEnough;
    };

    // 폼 제출 핸들러
    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!validatePassword(formData.password)) {
            alert('비밀번호는 8자 이상, 영문 대/소문자, 특수문자를 포함해야 합니다.');
            return;
        }

        if (!formData.termsAgreed) {
            alert('서비스 이용약관에 동의해주세요.');
            return;
        }

        // 여기에 회원가입 로직 추가
        console.log('Form submitted:', formData);
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50">
            <div className="bg-white rounded-lg w-full max-w-md p-6 relative">
                {/* Header */}
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-xl font-bold">회원가입</h2>
                    <button
                        onClick={hideModal}
                        className="p-2 hover:bg-gray-100 rounded-full"
                    >
                        <X size={24} />
                    </button>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Nickname Input */}
                    <div>
                        <label className="block text-sm font-medium mb-2">
                            닉네임 <span className="text-red-500">*</span>
                        </label>
                        <input
                            type="text"
                            value={formData.nickname}
                            onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
                            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="닉네임을 입력해주세요"
                            required
                        />
                    </div>

                    {/* Password Input */}
                    <div>
                        <label className="block text-sm font-medium mb-2">
                            비밀번호 <span className="text-red-500">*</span>
                        </label>
                        <input
                            type="password"
                            value={formData.password}
                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="비밀번호를 입력해주세요"
                            required
                        />
                        <p className="mt-1 text-sm text-gray-500">
                            8자 이상, 영문 대/소문자, 특수문자를 포함해야 합니다.
                        </p>
                    </div>

                    {/* Terms Agreement */}
                    <div className="flex items-center gap-2">
                        <input
                            type="checkbox"
                            checked={formData.termsAgreed}
                            onChange={(e) => setFormData({ ...formData, termsAgreed: e.target.checked })}
                            className="w-4 h-4 rounded text-blue-600 focus:ring-blue-500"
                            required
                        />
                        <label className="text-sm">
                            서비스 이용약관에 동의합니다 <span className="text-red-500">*</span>
                        </label>
                    </div>

                    {/* Submit Button */}
                    <button
                        type="submit"
                        className="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        가입하기
                    </button>
                </form>
            </div>
        </div>
    );
};