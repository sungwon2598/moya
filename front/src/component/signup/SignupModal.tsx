import React from 'react';
import { X } from 'lucide-react';
import { useModal } from '../../hooks/useModal';
import { useAuth } from '../../context/AuthContext';
import { GoogleLoginButton } from './GoogleLoginButton';


export const SignupModal: React.FC = () => {
    const { hideModal } = useModal();
    const { isLoading, error } = useAuth();

    // 로그인 성공 핸들러 - 실제 사용되는 로직 추가
    const handleGoogleLoginSuccess = () => {
        hideModal();
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50">
            <div className="bg-white rounded-lg w-full max-w-md p-6 relative">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-xl font-bold">회원가입</h2>
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

                <div className="text-center mb-6">
                    <p className="text-gray-600">
                        MOYA에 오신 것을 환영합니다
                    </p>
                    <p className="text-sm text-gray-500 mt-2">
                        Google 계정으로 간편하게 시작하세요
                    </p>
                </div>

                <GoogleLoginButton
                    isLoading={isLoading}
                    onGoogleLoginSuccess={handleGoogleLoginSuccess}
                />
            </div>
        </div>
    );
};