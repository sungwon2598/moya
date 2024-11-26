import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { AUTH_API } from '../../config/apiConfig';
import { LoadingSpinner } from '../../component/common/chat/LoadingSpinner';

export const OAuthCallback: React.FC = () => {
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

                if (response.data.nextStep === 'SIGNUP') {
                    navigate('/signup', {
                        state: {
                            token: response.data.accessToken
                        }
                    });
                } else {
                    login({
                        token: response.data.accessToken,
                        memberInfo: response.data.memberInfo!
                    });
                    navigate('/');
                }
            } catch (error) {
                console.error('OAuth callback error:', error);
                navigate('/login');
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
