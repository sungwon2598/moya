import React from 'react';
import { MapPin, Users, Wrench } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const hexToRgba = (hex: string, alpha: number): string => {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

const MainContent: React.FC = () => {
    const { isLoggedIn } = useAuth();
    const navigate = useNavigate();

    const actionButtons = [
        {
            title: '로드맵 생성',
            description: '나만의 학습 경로를 만들어보세요',
            icon: MapPin,
            bgColor: 'bg-blue-100',
            iconColor: 'text-blue-600',
            onClick: () => navigate('/roadmap-preview')
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

    return (
        <main className="bg-gray-50 flex flex-col items-center justify-center px-4">
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
                        <h1 className="text-6xl font-bold text-blue-600 mb-4">
                            MOYA
                        </h1>
                        <p className="text-xl text-gray-600 max-w-lg mb-8">
                            학습 성장을 위한 최적의 스터디 플랫폼,
                            <br />
                            MOYA와 함께 성장하는 여정을 시작하세요
                        </p>
                    </div>
                </div>
            </div>

            {/* 액션 버튼 그리드 - 로그인 시에만 표시 */}
            {isLoggedIn && (
                <div className="w-full max-w-5xl grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
                    {actionButtons.map((button, index) => (
                        <button
                            key={index}
                            onClick={button.onClick}  // 두 번째 추가할 부분
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
                        </button>
                    ))}
                </div>
            )}

            {/* 비로그인 시 회원가입 버튼 */}
            {!isLoggedIn && (
                <div className="w-full max-w-4xl flex flex-col items-center gap-4 mb-12">
                    <button
                        onClick={() => navigate('/register')}
                        className="px-8 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition-all duration-300 flex items-center group shadow-lg"
                    >
                        회원가입
                        <svg
                            className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M9 5l7 7-7 7"
                            />
                        </svg>
                    </button>
                    <span className="text-gray-500">
                        이미 계정이 있으신가요?{' '}
                        <button
                            onClick={() => navigate('/login')}
                            className="text-blue-600 hover:text-blue-700 font-medium"
                        >
                            로그인
                        </button>
                    </span>
                </div>
            )}
        </main>
    );
};

export default MainContent;