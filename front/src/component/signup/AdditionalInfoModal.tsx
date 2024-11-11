import React, { useState } from 'react';
import { X } from 'lucide-react';
import { useModal } from '../../hooks/useModal';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../config/apiConfig';

interface AdditionalInfoModalProps {
    initialNickname: string;
    email: string;
    googleToken: string;
}

export const AdditionalInfoModal: React.FC<AdditionalInfoModalProps> = ({
                                                                            initialNickname,email,
                                                                            googleToken
                                                                        }) => {
    const { hideModal } = useModal();
    const {  isLoading } = useAuth();

    const [formData, setFormData] = useState({
        nickname: initialNickname,
        password: '',
        passwordConfirm: '',
        termsAgreed: false
    });
    const [error, setError] = useState<string | null>(null);

    // 비밀번호 유효성 검사
    const validatePassword = (password: string): boolean => {
        const hasUpperCase = /[A-Z]/.test(password);
        const hasLowerCase = /[a-z]/.test(password);
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
        const isLongEnough = password.length >= 8;

        return hasUpperCase && hasLowerCase && hasSpecialChar && isLongEnough;
    };

    // 폼 제출 핸들러
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        // 비밀번호 확인
        if (formData.password !== formData.passwordConfirm) {
            setError('비밀번호가 일치하지 않습니다.');
            return;
        }

        if (!validatePassword(formData.password)) {
            setError('비밀번호는 8자 이상, 영문 대/소문자, 특수문자를 포함해야 합니다.');
            return;
        }

        if (!formData.termsAgreed) {
            setError('서비스 이용약관에 동의해주세요.');
            return;
        }

        try {
            // 회원가입 완료 API 호출
            await api.post('/api/auth/register/complete', {
                nickname: formData.nickname,
                password: formData.password,
                googleToken,
                email
            });

            // 성공적으로 가입 완료
            hideModal();
            // 필요한 경우 자동 로그인 처리
        } catch (err) {
            console.error('Registration error:', err);
            setError('회원가입 중 오류가 발생했습니다.');
        }
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50">
            <div className="bg-white rounded-lg w-full max-w-md p-6 relative">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-xl font-bold">추가 정보 입력</h2>
                    <button
                        onClick={hideModal}
                        className="p-2 hover:bg-gray-100 rounded-full"
                    >
                        <X size={24} />
                    </button>
                </div>

                {error && (
                    <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-lg">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
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

                    <div>
                        <label className="block text-sm font-medium mb-2">
                            비밀번호 확인 <span className="text-red-500">*</span>
                        </label>
                        <input
                            type="password"
                            value={formData.passwordConfirm}
                            onChange={(e) => setFormData({ ...formData, passwordConfirm: e.target.value })}
                            className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="비밀번호를 다시 입력해주세요"
                            required
                        />
                    </div>

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

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:bg-blue-400"
                    >
                        {isLoading ? '처리중...' : '가입 완료'}
                    </button>
                </form>
            </div>
        </div>
    );
};