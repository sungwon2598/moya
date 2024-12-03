import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { LoadingSpinner } from '@/component/common/LoadingSpinner';

const OAuthCallback: React.FC = () => {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [searchParams] = useSearchParams();
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const processOAuthResponse = async () => {
            try {
                // URL 파라미터에서 데이터 추출
                const accessToken = searchParams.get('accessToken');
                const refreshToken = searchParams.get('refreshToken');
                const nextStep = searchParams.get('nextStep');
                const isNewUser = searchParams.get('isNewUser');

                if (!accessToken) {
                    throw new Error('인증 토큰이 없습니다.');
                }

                if (nextStep === 'SIGNUP' && isNewUser === 'true') {
                    // 신규 회원인 경우
                    navigate('/signup', {
                        state: {
                            tempToken: accessToken,
                            nextStep: 'SIGNUP'
                        },
                        replace: true
                    });
                } else {
                    // 기존 회원인 경우
                    await login({
                        token: accessToken,
                        refreshToken: refreshToken || undefined
                    });
                    navigate('/', { replace: true });
                }
            } catch (error) {
                console.error('OAuth 처리 실패:', error);
                setError('로그인 처리 중 오류가 발생했습니다.');
                setTimeout(() => {
                    navigate('/', {
                        state: { error: '로그인 처리 중 오류가 발생했습니다.' },
                        replace: true
                    });
                }, 2000);
            }
        };

        processOAuthResponse();
    }, [navigate, login, searchParams]);

    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-center">
                    <p className="text-red-600">{error}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center">
            <div className="text-center">
                <LoadingSpinner />
                <p className="mt-4 text-gray-600">로그인 처리 중입니다...</p>
            </div>
        </div>
    );
};

export default OAuthCallback;
