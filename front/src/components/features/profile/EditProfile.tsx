import React from 'react';
import { useAuth } from '../auth/hooks/useAuth';
import { ProfileForm } from '../../shared';
import type { ProfileFormData } from '@core/types/profile';

const EditProfile: React.FC = () => {
  const { isAuthenticated: isLoggedIn } = useAuth(); // isLogin을 isLoggedIn으로 alias

  const handleSubmit = async (data: ProfileFormData) => {
    try {
      // API 호출 로직
      console.log('Submitting profile data:', data);
    } catch (error) {
      console.error('Failed to update profile:', error);
    }
  };

  if (!isLoggedIn) {
    return <div>로그인이 필요합니다.</div>;
  }

  return <ProfileForm onSubmit={handleSubmit} />;
};

export default EditProfile;
