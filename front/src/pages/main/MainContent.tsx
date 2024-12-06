import React from 'react';
import { MapPin, Users, Wrench } from 'lucide-react';
import { useAuth } from '../../features/auth/hooks/useAuth';
import { GoogleLoginButton } from '../../features/auth/components/GoogleLoginButton';

const hexToRgba = (hex: string, alpha: number): string => {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

const MainContent: React.FC = () => {
    const { isLogin, loading } = useAuth();

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
            iconColor: 'text-green-600'
        },
        {
            title: '협업툴',
            description: '효율적인 협업 도구를 활용해보세요',
            icon: Wrench,
            bgColor: 'bg-purple-100',
            iconColor: 'text-purple-600'
        }
    ];

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    return (
        <main className="bg-gray-50 flex flex-col items-center justify-center px-4 min-h-screen py-16">
            {/* 메인 포스터 섹션 */}
            <div className="w-full max-w-5xl mb-16">
                <div className="relative aspect-video bg-white rounded-2xl shadow-lg overflow-hidden">
                    {/* 그라데이션 배경 */}
                    <div className="absolute inset-0 bg-gradient-to-br from-blue-50 to-green-50">
                        {/* X 패턴 */}
                        <div className="absolute inset-0" style={{
                            backgroundImage: `linear-gradient(45deg, transparent 45%, ${hexToRgba('#0752cd', 0.1)} 45%, ${hexToRgba('#0752cd', 0.1)} 55%, transparent 55%)`,
                            backgroundSize: '30px 30px'
                        }}></div>
                    </div>

                    {/* 중앙 텍스트 */}
                    <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
                        <h1 className="text-4xl md:text-6xl font-bold text-blue-600 mb-4">
                            MOYA
                        </h1>
                        <p className="text-lg md:text-xl text-gray-600 max-w-lg mb-8 px-4">
                            학습 성장을 위한 최적의 스터디 플랫폼,
                            <br />
                            MOYA와 함께 성장하는 여정을 시작하세요
                        </p>
                    </div>
                </div>
            </div>

            {/* 액션 버튼 그리드 - 로그인 시에만 표시 */}
            {isLogin && (
                <div className="w-full max-w-5xl grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
                    {actionButtons.map((button, index) => (
                        <button
                            key={index}
                            className="group relative p-6 bg-white rounded-xl shadow-md hover:shadow-lg transition-all duration-300 text-left"
                        >
                            <div className={`${button.bgColor} w-12 h-12 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform`}>
                                <button.icon className={`w-6 h-6 ${button.iconColor}`} />
                            </div>
                            <h3 className="text-lg font-semibold text-gray-900 mb-2">
                                {button.title}
                            </h3>
                            <p className="text-gray-600 text-sm">
                                {button.description}
                            </p>
                            <div className="absolute inset-0 rounded-xl border-2 border-transparent group-hover:border-blue-100 transition-colors"></div>
                        </button>
                    ))}
                </div>
            )}

            {/* 비로그인 시 구글 로그인 섹션 */}
            {!isLogin && (
                <div className="w-full max-w-md flex flex-col items-center gap-6 mb-12">
                    <div className="text-center mb-2">
                        <h2 className="text-2xl font-semibold text-gray-900 mb-2">
                            MOYA 시작하기
                        </h2>
                        <p className="text-gray-600 mb-6">
                            Google 계정으로 간편하게 시작해보세요
                        </p>
                    </div>

                    {/* Google 로그인 버튼 */}
                    <div className="w-full flex justify-center items-center">
                        <GoogleLoginButton />
                    </div>

                    <p className="text-sm text-gray-500 text-center max-w-sm px-4">
                        계속 진행하면 MOYA의{' '}
                        <a href="/terms" className="text-blue-600 hover:underline">
                            이용약관
                        </a>
                        {' '}및{' '}
                        <a href="/privacy" className="text-blue-600 hover:underline">
                            개인정보처리방침
                        </a>
                        에 동의하는 것으로 간주됩니다.
                    </p>
                </div>
            )}
        </main>
    );
};

export default MainContent;
