import React, { useState } from 'react';
import { useAuth } from '../../components/features/auth/hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import LogoIcon from '@/assets/logo.svg?react';
import { useGoogleLoginPopup } from '@/components/features/auth/hooks/useGoogleLoginPopup';
import KeywordAni from '@/components/features/main/KeywordAni';

const MainContent: React.FC = () => {
  const { isAuthenticated, loading, handleGoogleLogin } = useAuth();
  const navigate = useNavigate();
  const triggerGoogleLogin = useGoogleLoginPopup();
  const [loginLoading, setLoginLoading] = useState(false);

  const handleRoadmapClick = async () => {
    if (loginLoading) return;

    if (isAuthenticated) {
      navigate('/roadmap/create');
      return;
    }

    setLoginLoading(true);
    try {
      const authData = await triggerGoogleLogin();
      await handleGoogleLogin(authData);
      navigate('/roadmap/create');
    } catch (error) {
      if ((error as Error).name === 'AbortError') {
        console.log('Login process aborted by user.');
      } else {
        console.error('Google 로그인 실패:', error);
        alert('로그인 중 문제가 발생했습니다.');
      }
    } finally {
      setLoginLoading(false);
    }
  };
  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="relative h-[calc(100vh-60px)] pt-12">
      <div className="overflow-hidden">
        <KeywordAni />
      </div>
      <div className="z-10 text-center">
        <LogoIcon className="mx-auto mb-2 h-12 w-auto fill-black dark:fill-white" />
        <p className="text-base font-light tracking-wide opacity-30">AI study partner</p>
      </div>
      <div className="z-10 mx-auto mb-12 text-center sm:w-2/3">
        <h1 className="font-heading my-6">
          당신의 성장을 설계하는 <br /> AI 스터디 파트너
        </h1>
        <p className="mx-auto px-4 text-sm/6 opacity-60 sm:text-base/6">
          MOYA는 스터디 그룹 구성부터 학습 계획 수립까지, 학습의 전 과정을 AI 기반으로 지원하는 플랫폼입니다.
          <br /> 개인의 목표와 일정에 최적화된 플랜을 자동으로 생성하고, 곧 실시간 온라인 스터디 기능까지 제공할
          예정입니다.
        </p>
        <p className="mx-auto pt-4 text-sm/7 opacity-60 sm:text-base/6">
          MOYA와 함께, 더 스마트하고 효율적인 학습을 시작하세요.
        </p>
      </div>

      <button
        type="button"
        onClick={handleRoadmapClick}
        className="bg-moya-primary absolute bottom-0 z-10 flex h-16 w-full cursor-pointer items-center justify-center gap-1">
        <h5 className="font-black">나만의 로드맵 생성하기</h5>
        <div className="flex items-center justify-center">
          <span className="material-symbols-outlined !text-[16px] opacity-30">arrow_forward_ios</span>
          <span className="material-symbols-outlined -ml-1.5 !text-[16px] opacity-60">arrow_forward_ios</span>
          <span className="material-symbols-outlined -ml-1.5 !text-[16px] opacity-90">arrow_forward_ios</span>
        </div>
      </button>
    </div>
  );
};

export default MainContent;
