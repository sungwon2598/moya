
import React, { useState } from 'react';
import { useModal } from '../../hooks/useModal';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../config/apiConfig';

interface AdditionalInfoModalProps {
    initialNickname: string;
    email: string;
    googleToken: string;
}

export const AdditionalInfoModal: React.FC<AdditionalInfoModalProps> = ({
                                                                            initialNickname,
                                                                            email,

                                                                        }) => {
    const { hideModal } = useModal();
    const { login } = useAuth();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [formData, setFormData] = useState({
        nickname: initialNickname,
        termsAgreed: false
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setIsLoading(true);

        if (!formData.termsAgreed) {
            setError('서비스 이용약관에 동의해주세요.');
            setIsLoading(false);
            return;
        }

        try {
            // 회원가입 API 호출
            const response = await api.post('/api/auth/register', {
                nickname: formData.nickname,
                email,
                termsAgreed: formData.termsAgreed
            });

            // JWT 토큰 저장 및 로그인 처리
            const { token } = response.data;
            login(token);
            hideModal();
        } catch (err: any) {
            console.error('Registration error:', err);
            setError(err.response?.data?.message || '회원가입 중 오류가 발생했습니다.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="w-full max-w-md">
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
                        className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-moya-primary"
                        required
                    />
                </div>

                <div className="flex items-center gap-2">
                    <input
                        type="checkbox"
                        checked={formData.termsAgreed}
                        onChange={(e) => setFormData({ ...formData, termsAgreed: e.target.checked })}
                        className="w-4 h-4 rounded text-moya-primary"
                        required
                    />
                    <label className="text-sm">
                        서비스 이용약관에 동의합니다 <span className="text-red-500">*</span>
                    </label>
                </div>

                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full py-3 bg-moya-primary text-white rounded-lg hover:bg-moya-secondary transition-colors disabled:bg-opacity-50"
                >
                    {isLoading ? '처리중...' : '가입 완료'}
                </button>
            </form>
        </div>
    );
};