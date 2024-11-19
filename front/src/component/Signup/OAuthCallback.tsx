
// OAuthCallback.tsx
import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useModal } from '../../hooks/useModal';
import { useAuth } from '../../context/AuthContext';
import { AdditionalInfoModal } from './AdditionalInfoModal';

export const OAuthCallback: React.FC = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { showModal } = useModal();
    const { login } = useAuth();

    useEffect(() => {
        const token = searchParams.get('token');
        const error = searchParams.get('error');
        const requiresSignup = searchParams.get('requiresSignup');
        const email = searchParams.get('email');
        const name = searchParams.get('name');

        if (error) {
            console.error('Authentication error:', error);
            navigate('/');
            return;
        }

        if (requiresSignup === 'true' && email && name) {
            // 회원가입이 필요한 경우
            showModal(
                <AdditionalInfoModal
                    initialNickname={name}
                    email={email}
                    googleToken={token || ''}
                />,
                {
                    title: '추가 정보 입력',
                    size: 'md',
                    showCloseButton: true
                }
            );
        } else if (token) {
            // 이미 가입된 회원인 경우
            login(token);
            navigate('/');
        }
    }, [searchParams, navigate, showModal, login]);

    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-moya-primary"></div>
        </div>
    );
};