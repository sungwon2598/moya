// OAuthCallback.tsx
import React, { useEffect } from 'react';
import {  useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import {LoadingSpinner} from "../../component/common/LoadingSpinner.tsx";
// src/pages/auth/OAuthCallback.tsx
const OAuthCallback: React.FC = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    useEffect(() => {
        const processResponse = () => {
            try {
                // 브라우저에 표시된 JSON 응답 파싱
                const jsonText = document.body.textContent || '';
                const response = JSON.parse(jsonText);

                // 신규 회원인 경우
                if (response.nextStep === 'SIGNUP') {
                    navigate('/signup', {
                        state: {
                            tempToken: response.accessToken,
                        },
                        replace: true // 현재 페이지를 히스토리에서 교체
                    });
                } else {
                    // 기존 회원인 경우
                    login({
                        token: response.accessToken,
                        memberInfo: response.memberInfo
                    });
                    navigate('/', { replace: true });
                }
            } catch (error) {
                console.error('OAuth 응답 처리 실패:', error);
                navigate('/login', {
                    state: { error: '인증 처리 중 오류가 발생했습니다.' }
                });
            }
        };

        processResponse();
    }, []);

    // 로딩 상태 표시 (JSON이 잠시 보이는 것을 방지)
    return (
        <div className="min-h-screen flex items-center justify-center">
            <LoadingSpinner />
        </div>
    );
};

export default OAuthCallback;
