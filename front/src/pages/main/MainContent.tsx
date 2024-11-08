
import React from 'react';
import { Home, Book, User } from 'lucide-react';
import { Feature } from '../../types/types.tsx';

const MainContent: React.FC = () => {
    const features: Feature[] = [
        {
            title: "맞춤형 로드맵",
            description: "당신의 수준과 목표에 맞는 최적의 학습 경로를 제공합니다.",
            icon: Home,
            iconBgColor: "bg-green-100",
            iconColor: "text-green-600"
        },
        {
            title: "실전 스터디",
            description: "다른 개발자들과 함께 성장하는 스터디 그룹에 참여하세요.",
            icon: Book,
            iconBgColor: "bg-blue-100",
            iconColor: "text-blue-600"
        },
        {
            title: "커뮤니티",
            description: "활발한 개발자 커뮤니티에서 지식과 경험을 공유하세요.",
            icon: User,
            iconBgColor: "bg-purple-100",
            iconColor: "text-purple-600"
        }
    ];

    return (
        <main className="pt-16 min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-12">
                <div className="max-w-4xl mx-auto">
                    <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
                        <div className="relative">
                            <div className="absolute inset-0 bg-gradient-to-br from-green-50 to-blue-50">
                                <div className="absolute inset-0" style={{
                                    backgroundImage: 'linear-gradient(45deg, transparent 45%, #e5e7eb 45%, #e5e7eb 55%, transparent 55%)',
                                    backgroundSize: '20px 20px'
                                }}></div>
                            </div>

                            <div className="relative px-8 py-16 text-center">
                                <h1 className="text-5xl font-bold text-gray-900 mb-6">
                                    MOYA
                                    <span className="text-green-600">.</span>
                                </h1>
                                <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
                                    개발자의 성장을 위한 최적의 플랫폼
                                    <br />
                                    나만의 학습 경로를 만들고 함께 성장하세요
                                </p>
                                <div className="space-x-4">
                                    <button className="bg-green-600 text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-green-700 transition-colors duration-200 shadow-md hover:shadow-lg">
                                        무료로 시작하기
                                    </button>
                                    <button className="bg-white text-green-600 px-8 py-3 rounded-lg text-lg font-semibold border-2 border-green-600 hover:bg-green-50 transition-colors duration-200">
                                        더 알아보기
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div className="grid md:grid-cols-3 gap-8 px-8 py-12 bg-white">
                            {features.map((feature, index) => (
                                <div key={index} className="text-center">
                                    <div className={`w-16 h-16 ${feature.iconBgColor} rounded-full flex items-center justify-center mx-auto mb-4`}>
                                        <feature.icon className={`w-8 h-8 ${feature.iconColor}`} />
                                    </div>
                                    <h3 className="text-lg font-semibold mb-2">{feature.title}</h3>
                                    <p className="text-gray-600">{feature.description}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </main>
    );
};

export default MainContent;