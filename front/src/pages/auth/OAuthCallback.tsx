// OAuthCallback.tsx
import React, { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { AUTH_API } from '@/config/apiConfig';
import {LoadingSpinner} from "../../component/common/LoadingSpinner.tsx";

const OAuthCallback: React.FC = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { login } = useAuth();

    useEffect(() => {
        const handleCallback = async () => {
            const code = searchParams.get('code');
            if (!code) {
                navigate('/', {
                    state: { error: '인증 코드가 없습니다.' }
                });
                return;
            }

            try {
                // handleGoogleCallback 대신 handleOAuthCallback 사용
                const response = await AUTH_API.handleOAuthCallback(code);

                if (response.nextStep === 'SIGNUP') {
                    navigate('/signup', {
                        state: {
                            tempToken: response.accessToken
                        },
                        replace: true
                    });
                } else {
                    login({
                        token: response.accessToken,
                        memberInfo: response.memberInfo!
                    });
                    navigate('/', { replace: true });
                }
            } catch (error) {
                console.error('OAuth callback error:', error);
                navigate('/', {
                    state: { error: '로그인 처리 중 오류가 발생했습니다.' }
                });
            }
        };

        handleCallback();
    }, [searchParams]);

    return (
        <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner />
        </div>
    );
};

export default OAuthCallback;
