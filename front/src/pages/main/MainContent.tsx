import React from 'react';
import { MapPin, Users, Wrench } from 'lucide-react';
import { useAuth } from '../../features/auth/hooks/useAuth';
import { GoogleLoginButton } from '../../features/auth/components/GoogleLoginButton';
import { useNavigate } from 'react-router-dom';

const hexToRgba = (hex: string, alpha: number): string => {
  const r = parseInt(hex.slice(1, 3), 16);
  const g = parseInt(hex.slice(3, 5), 16);
  const b = parseInt(hex.slice(5, 7), 16);
  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

const MainContent: React.FC = () => {
  const { isAuthenticated, loading, handleGoogleLogin } = useAuth();
  const navigate = useNavigate();

  const actionButtons = [
    {
      title: '로드맵 생성',
      description: '나만의 학습 경로를 만들어보세요',
      icon: MapPin,
      bgColor: 'bg-blue-100',
      iconColor: 'text-blue-600',
    },
    {
      title: '스터디 찾기',
      description: '함께 성장할 동료를 찾아보세요',
      icon: Users,
      bgColor: 'bg-green-100',
      iconColor: 'text-green-600',
    },
    {
      title: '협업툴',
      description: '효율적인 협업 도구를 활용해보세요',
      icon: Wrench,
      bgColor: 'bg-purple-100',
      iconColor: 'text-purple-600',
    },
  ];

  const handleLoginSuccess = async (authData: any) => {
    try {
      await handleGoogleLogin(authData);
      navigate('/');
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="w-8 h-8 border-b-2 border-blue-600 rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50">
      <div className="max-w-6xl px-4 py-8 mx-auto">
        {/* 메인 포스터 섹션 */}
        <div className="mb-12">
          <div className="relative overflow-hidden bg-white shadow-lg aspect-video rounded-2xl">
            {/* 그라데이션 배경 */}
            <div className="absolute inset-0 bg-gradient-to-br from-blue-50 to-green-50">
              {/* X 패턴 */}
              <div
                className="absolute inset-0"
                style={{
                  backgroundImage: `linear-gradient(45deg, transparent 45%, ${hexToRgba('#0752cd', 0.1)} 45%, ${hexToRgba('#0752cd', 0.1)} 55%, transparent 55%)`,
                  backgroundSize: '30px 30px',
                }}></div>
            </div>

            {/* 중앙 텍스트 */}
            <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
              <h1 className="mb-4 text-4xl font-bold text-blue-600 md:text-6xl">MOYA</h1>
              <p className="max-w-lg px-4 mb-8 text-lg text-gray-600 md:text-xl">
                학습 성장을 위한 최적의 스터디 플랫폼,
                <br />
                MOYA와 함께 성장하는 여정을 시작하세요
              </p>
            </div>
          </div>
        </div>

        {/* 액션 버튼 그리드 - 로그인 시에만 표시 */}
        {isAuthenticated && (
          <div className="grid grid-cols-1 gap-6 mb-12 md:grid-cols-3">
            {actionButtons.map((button, index) => (
              <button
                key={index}
                className="relative p-6 text-left transition-all duration-300 bg-white shadow-md group rounded-xl hover:shadow-lg">
                <div
                  className={`${button.bgColor} mb-4 flex h-12 w-12 items-center justify-center rounded-lg transition-transform group-hover:scale-110`}>
                  <button.icon className={`h-6 w-6 ${button.iconColor}`} />
                </div>
                <h3 className="mb-2 text-lg font-semibold text-gray-900">{button.title}</h3>
                <p className="text-sm text-gray-600">{button.description}</p>
                <div className="absolute inset-0 transition-colors border-2 border-transparent rounded-xl group-hover:border-blue-100"></div>
              </button>
            ))}
          </div>
        )}

        {/* 비로그인 시 구글 로그인 섹션 */}
        {!isAuthenticated && (
          <div className="flex flex-col items-center gap-6 mb-12">
            <div className="mb-2 text-center">
              <h2 className="mb-2 text-2xl font-semibold text-gray-900">MOYA 시작하기</h2>
              <p className="mb-6 text-gray-600">Google 계정으로 간편하게 시작해보세요</p>
            </div>

            {/* Google 로그인 버튼 */}
            <div className="flex items-center justify-center w-full">
              <GoogleLoginButton
                theme="filled_blue"
                size="large"
                onSuccess={handleLoginSuccess}
                onError={(error) => console.error('로그인 실패:', error)}
              />
            </div>

            <p className="max-w-sm px-4 text-sm text-center text-gray-500">
              계속 진행하면 MOYA의{' '}
              <a href="/terms" className="text-blue-600 hover:underline">
                이용약관
              </a>{' '}
              및{' '}
              <a href="/privacy" className="text-blue-600 hover:underline">
                개인정보처리방침
              </a>
              에 동의하는 것으로 간주됩니다.
            </p>
          </div>
        )}
      </div>
    </main>
  );
};

export default MainContent;
