// src/pages/profile/EditProfile.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import ProfileForm from '@/component/profile/ProfileForm';

export default function EditProfile() {
    const navigate = useNavigate();
    const { isLoggedIn } = useAuth();

    // 비로그인 상태일 경우 메인 페이지로 리다이렉트
    React.useEffect(() => {
        if (!isLoggedIn) {
            navigate('/', { replace: true });
        }
    }, [isLoggedIn, navigate]);

    if (!isLoggedIn) {
        return null;
    }

    return <ProfileForm />;
}
