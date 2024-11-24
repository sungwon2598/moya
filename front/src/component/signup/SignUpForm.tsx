import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { AUTH_API, SignUpFormData } from '../../config/apiConfig';
import Alert from './Alert';

interface SignUpFormProps {
    tempToken: string;
}

export const SignUpForm: React.FC<SignUpFormProps> = ({ tempToken }) => {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [formData, setFormData] = useState<SignUpFormData>({
        nickname: '',
        introduction: '',
        marketingAgreed: false
    });
    const [error, setError] = useState<string | null>(null);
    const [isChecking, setIsChecking] = useState(false);
    const [isNicknameValid, setIsNicknameValid] = useState(false);

    const handleNicknameCheck = async () => {
        if (formData.nickname.length < 2) {
            setError('닉네임은 2자 이상이어야 합니다.');
            return;
        }

        setIsChecking(true);
        try {
            const isAvailable = await AUTH_API.checkNickname(formData.nickname);
            setIsNicknameValid(isAvailable);
            setError(isAvailable ? null : '이미 사용중인 닉네임입니다.');
        } catch (error) {
            setError('닉네임 중복 확인 중 오류가 발생했습니다.');
            setIsNicknameValid(false);
        } finally {
            setIsChecking(false);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!isNicknameValid) {
            setError('닉네임 중복 확인이 필요합니다.');
            return;
        }

        try {
            const response = await AUTH_API.signUpComplete(tempToken, formData);
            login(response);
            navigate('/');
        } catch (error) {
            setError('회원가입 처리 중 오류가 발생했습니다.');
        }
    };

    return (
        <div className="w-full space-y-6">
            {error && <Alert variant="error">{error}</Alert>}

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        닉네임 *
                    </label>
                    <div className="flex gap-2">
                        <input
                            type="text"
                            value={formData.nickname}
                            onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
                            className="flex-1 rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-moya-primary"
                            placeholder="닉네임을 입력하세요"
                            minLength={2}
                            maxLength={20}
                            required
                        />
                        <button
                            type="button"
                            onClick={handleNicknameCheck}
                            disabled={isChecking}
                            className="px-4 py-2 bg-moya-primary text-white rounded-lg hover:bg-moya-secondary transition-colors disabled:opacity-50"
                        >
                            {isChecking ? '확인 중...' : '중복 확인'}
                        </button>
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        자기소개
                    </label>
                    <textarea
                        value={formData.introduction}
                        onChange={(e) => setFormData({ ...formData, introduction: e.target.value })}
                        className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-moya-primary"
                        placeholder="자기소개를 입력하세요"
                        rows={4}
                        maxLength={500}
                    />
                </div>

                <div className="flex items-start">
                    <input
                        type="checkbox"
                        id="marketing"
                        checked={formData.marketingAgreed}
                        onChange={(e) => setFormData({ ...formData, marketingAgreed: e.target.checked })}
                        className="mt-1 h-4 w-4 rounded border-gray-300 text-moya-primary focus:ring-moya-primary"
                    />
                    <label htmlFor="marketing" className="ml-2 text-sm text-gray-600">
                        마케팅 정보 수신에 동의합니다. (선택)
                    </label>
                </div>

                <button
                    type="submit"
                    disabled={!isNicknameValid}
                    className="w-full px-4 py-2 bg-moya-primary text-white rounded-lg hover:bg-moya-secondary transition-colors disabled:opacity-50"
                >
                    가입하기
                </button>
            </form>
        </div>
    );
};

export default SignUpForm;