import React from 'react';
import { useGoogleLogin, TokenResponse } from '@react-oauth/google';
import { api } from '../../config/apiConfig';
import { useModal } from '../../hooks/useModal';
import { AdditionalInfoModal } from './AdditionalInfoModal';

interface GoogleUserInfo {
    email: string;
    name: string;
    picture?: string;
    email_verified?: boolean;
}

interface GoogleLoginButtonProps {
    isLoading: boolean;
    onGoogleLoginSuccess?: (userInfo: GoogleUserInfo) => void;
}

export const GoogleLoginButton: React.FC<GoogleLoginButtonProps> = ({
                                                                        isLoading,
                                                                        onGoogleLoginSuccess
                                                                    }) => {
    const { showModal } = useModal();

    const googleLogin = useGoogleLogin({
        onSuccess: async (tokenResponse: TokenResponse) => {
            try {
                // 구글 토큰으로 사용자 정보 가져오기
                const userInfoResponse = await api.get<GoogleUserInfo>(
                    'https://www.googleapis.com/oauth2/v3/userinfo',
                    {
                        headers: { Authorization: `Bearer ${tokenResponse.access_token}` }
                    }
                );

                const userInfo = userInfoResponse.data;

                // 이메일 인증 확인
                if (!userInfo.email_verified) {
                    throw new Error('이메일 인증이 필요합니다.');
                }

                // 백엔드에 구글 인증 정보 전송
                const authResponse = await api.post('/api/auth/google/verify', {
                    googleToken: tokenResponse.access_token,
                    email: userInfo.email,
                    name: userInfo.name,
                    picture: userInfo.picture
                });

                if (authResponse.data.requiresAdditionalInfo) {
                    // 추가 정보 입력 모달 표시
                    showModal(
                        <AdditionalInfoModal
                            initialNickname={userInfo.name}
                            email={userInfo.email}
                            googleToken={tokenResponse.access_token}
                        />
                    );
                } else {
                    // 이미 가입된 사용자인 경우
                    onGoogleLoginSuccess?.(userInfo);
                }
            } catch (error) {
                console.error('Google login error:', error);
            }
        },
        onError: (error) => {
            console.error('Login Failed:', error);
        },
        flow: 'implicit',  // 암시적 흐름 사용
    });

    return (
        <button
            onClick={() => googleLogin()}
            disabled={isLoading}
            className="w-full h-12 px-4 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors flex items-center justify-center gap-3 disabled:opacity-50 disabled:cursor-not-allowed"
            style={{ boxShadow: '0 1px 3px rgba(0,0,0,0.08)' }}
        >
            <svg width="18" height="18" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48">
                <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
                <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
                <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
                <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
            </svg>
            <span className="font-medium text-gray-700">Google로 계속하기</span>
        </button>
    );
};