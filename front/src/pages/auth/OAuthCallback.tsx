import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { LoadingSpinner } from "../../component/common/LoadingSpinner";

const OAuthCallback: React.FC = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    useEffect(() => {
        const processResponse = async () => {
            try {
                const responseText = document.body.textContent;
                if (!responseText) {
                    throw new Error('응답 데이터가 없습니다.');
                }

                const response = JSON.parse(responseText);
                console.log('OAuth Response:', response);

                if (response.nextStep === 'SIGNUP') {
                    navigate('/signup', {
                        state: {
                            tempToken: response.accessToken,
                        },
                        replace: true
                    });
                } else {
                    login({
                        token: response.accessToken,
                        memberInfo: response.memberInfo
                    });
                    navigate('/', { replace: true });
                }
            } catch (error) {
                console.error('OAuth 응답 처리 실패:', error);
                navigate('/', {
                    state: {
                        error: '로그인 처리 중 오류가 발생했습니다.'
                    },
                    replace: true
                });
            }
        };

        processResponse();
    }, [navigate, login]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="text-center">
                <LoadingSpinner />
                <p className="mt-4 text-gray-600">로그인 처리 중입니다...</p>
            </div>
        </div>
    );
};

export default OAuthCallback;
