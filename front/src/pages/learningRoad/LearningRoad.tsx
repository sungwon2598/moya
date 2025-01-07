import React from 'react';
import { LayoutGrid, Monitor, Mic } from 'lucide-react';

interface RoadmapStep {
    id: number;
    title: string;
    completed: boolean;
}

const LearningRoad: React.FC = () => {
    const roadmapSteps: RoadmapStep[] = [
        { id: 1, title: 'Swift 기초', completed: true },
        { id: 2, title: 'UI 기초', completed: false },
        { id: 3, title: 'Auto Layout', completed: false },
        { id: 4, title: 'iOS Framework', completed: false },
        { id: 5, title: 'Core Data', completed: false },
        { id: 6, title: '앱 배포', completed: false }
    ];

    return (
        <div className="flex h-screen bg-gray-50">
            {/* Left LeftSidebar */}
            <div className="w-64 border-r border-gray-200 p-4">
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-blue-600">MOYA</h1>
                </div>

                {/* Navigation Menu */}
                <nav className="space-y-4">
                    <div className="flex items-center space-x-3 text-gray-600 hover:text-gray-900 hover:bg-gray-100 p-2 rounded-lg cursor-pointer">
                        <LayoutGrid size={24} />
                        <span>메인</span>
                    </div>
                    <div className="flex items-center space-x-3 text-gray-600 hover:text-gray-900 hover:bg-gray-100 p-2 rounded-lg cursor-pointer">
                        <Monitor size={24} />
                        <span>화이트보드</span>
                    </div>
                    <div className="flex items-center space-x-3 text-gray-600 hover:text-gray-900 hover:bg-gray-100 p-2 rounded-lg cursor-pointer">
                        <Mic size={24} />
                        <span>음성채팅</span>
                    </div>
                </nav>
            </div>

            {/* Main Content Area */}
            <div className="flex-1 p-8">
                <div className="mb-6">
                    <h2 className="text-2xl font-semibold text-gray-900 mb-2">iOS 개발 로드맵</h2>
                    <p className="text-gray-600">앱 개발의 기초부터 배포까지</p>
                </div>

                {/* Roadmap Path */}
                <div className="relative mt-12">
                    {roadmapSteps.map((step, index) => (
                        <div key={step.id} className="relative mb-16">
                            <div className="flex items-center">
                                <div className={`w-12 h-12 rounded-full flex items-center justify-center ${
                                    step.completed ? 'bg-blue-500 text-white' : 'bg-gray-200 text-gray-600'
                                }`}>
                                    <span className="text-xl">{step.id}</span>
                                </div>
                                <div className="ml-4">
                                    <h3 className="text-xl text-gray-900 font-medium">{step.title}</h3>
                                </div>
                            </div>
                            {index < roadmapSteps.length - 1 && (
                                <div className="absolute left-6 top-12 w-0.5 h-16 bg-gray-200"></div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default LearningRoad;