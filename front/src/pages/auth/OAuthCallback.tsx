import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { LoadingSpinner } from '@/component/common/LoadingSpinner';

interface OAuthResponse {
    accessToken: string;
    refreshToken: null;
    nextStep: 'SIGNUP' | 'LOGIN';
    newUser: boolean;
}

const OAuthCallback: React.FC = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const processOAuthResponse = () => {
            try {
                // 페이지에 렌더링된 JSON 문자열을 파싱
                const responseText = document.body.textContent;
                if (!responseText) return;

                const response = JSON.parse(responseText) as OAuthResponse;

                if (response.nextStep === 'SIGNUP') {
                    // 회원가입 폼으로 이동, temporaryToken 전달
                    navigate('/signup', {
                        state: {
                            tempToken: response.accessToken
                        },
                        replace: true // 히스토리 스택에서 현재 페이지 교체
                    });
                } else {
                    // 이미 가입된 회원인 경우 메인으로 이동
                    navigate('/', { replace: true });
                }
            } catch (error) {
                console.error('OAuth 응답 처리 중 오류:', error);
                navigate('/login', {
                    state: { error: '로그인 처리 중 오류가 발생했습니다.' },
                    replace: true
                });
            }
        };

        processOAuthResponse();
    }, [navigate]);

    // 처리 중일 때 보여줄 로딩 상태
    return (
        <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner />
        </div>
    );
};

export default OAuthCallback;
