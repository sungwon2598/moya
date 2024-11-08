import React from 'react';
import { MapPin, Users, Wrench } from 'lucide-react';

const MainContent: React.FC = () => {
    const actionButtons = [
        {
            title: '로드맵 생성',
            description: '나만의 학습 경로를 만들어보세요',
            icon: MapPin,
            bgColor: 'bg-moya-primary/10',
            iconColor: 'text-moya-primary'
        },
        {
            title: '스터디 찾기',
            description: '함께 성장할 동료를 찾아보세요',
            icon: Users,
            bgColor: 'bg-moya-secondary/10',
            iconColor: 'text-moya-secondary'
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
        <main className="min-h-screen bg-moya-background flex flex-col items-center justify-center px-4">
            {/* 메인 포스터 섹션 */}
            <div className="w-full max-w-5xl mb-16">
                <div className="relative aspect-video bg-white rounded-2xl shadow-lg overflow-hidden">
                    {/* 그라데이션 배경 */}
                    <div className="absolute inset-0 bg-gradient-to-br from-moya-primary/5 to-moya-secondary/5">
                        {/* X 패턴 */}
                        <div className="absolute inset-0" style={{
                            backgroundImage: `linear-gradient(45deg, transparent 45%, ${hexToRgba('#0752cd', 0.1)} 45%, ${hexToRgba('#0752cd', 0.1)} 55%, transparent 55%)`,
                            backgroundSize: '30px 30px'
                        }}></div>
                    </div>

                    {/* 데코레이션 요소 */}
                    <div className="absolute left-8 top-1/4">
                        <div className="w-10 h-10 bg-green-500 rounded-full shadow-lg animate-pulse"></div>
                    </div>

                    {/* 중앙 텍스트 */}
                    <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
                        <h1 className="text-6xl font-bold text-moya-primary mb-4">
                            MOYA
                        </h1>
                        <p className="text-xl text-gray-600 max-w-lg">
                            학습 성장을 위한 최적의 스터디 플랫폼,
                            <br />
                            MOYA와 함께 성장하는 여정을 시작하세요
                        </p>
                    </div>

                    {/* 플로팅 요소들 */}
                    <div className="absolute right-12 bottom-12 bg-white p-4 rounded-lg shadow-xl transform rotate-6 animate-float">
                        <div className="w-32 h-24 bg-moya-secondary/10 rounded-md"></div>
                    </div>
                    <div className="absolute left-12 bottom-20 bg-white p-3 rounded-lg shadow-xl transform -rotate-3 animate-float-delayed">
                        <div className="w-24 h-16 bg-green-100 rounded-md"></div>
                    </div>
                </div>
            </div>

            {/* 액션 버튼 그리드 */}
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
                        {/* 호버 시 나타나는 화살표 */}
                        <div className="absolute right-6 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity">
                            <div className={`w-8 h-8 ${button.bgColor} rounded-full flex items-center justify-center`}>
                                <svg className={`w-4 h-4 ${button.iconColor}`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                </svg>
                            </div>
                        </div>
                    </button>
                ))}
            </div>

            {/* 회원가입 버튼 */}
            <button className="w-full max-w-md bg-white hover:bg-gray-50 text-gray-700 px-6 py-4 rounded-xl shadow-md transition-all duration-300 flex items-center justify-center group">
                <span className="inline-block bg-red-500 text-white px-2 py-0.5 text-sm rounded mr-3">D</span>
                무료 회원가입 하기
                <svg className="ml-2 w-5 h-5 opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
            </button>
        </main>
    );
};

// 유틸리티 함수: HEX 컬러를 RGBA로 변환
const hexToRgba = (hex: string, alpha: number) => {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

export default MainContent;