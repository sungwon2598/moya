import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { AUTH_API } from '@/config/apiConfig';
import { LoadingSpinner } from '../../component/common/LoadingSpinner.tsx';

export  const OAuthCallback: React.FC = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { login } = useAuth();

    useEffect(() => {
        const processOAuthCallback = async () => {
            try {
                const code = searchParams.get('code');
                if (!code) {
                    throw new Error('Authorization code not found');
                }

                const response = await AUTH_API.handleOAuthCallback(code);

                if (response.nextStep === 'SIGNUP') {
                    // 신규 회원인 경우
                    navigate('/signup', {
                        state: {
                            tempToken: response.accessToken
                        }
                    });
                } else {
                    // 기존 회원인 경우
                    login({
                        token: response.accessToken,
                        memberInfo: response.memberInfo
                    });
                    navigate('/');
                }
            } catch (error) {
                console.error('OAuth callback error:', error);
                navigate('/login', {
                    state: { error: 'Google 로그인 중 오류가 발생했습니다.' }
                });
            }
        };

        processOAuthCallback();
    }, [searchParams, navigate, login]);

    return (
        <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner />
        </div>
    );
};

export default OAuthCallback;
