import { ArrowRight } from 'lucide-react';
import { FC } from 'react';
import { RoadmapCard } from '../component/roadmap/RoadmapCard';
import { roadmapSamples } from '../data/roadmap-samples';

const RoadmapPreview: FC = () => {
    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-6xl mx-auto px-4 py-8">
                <div className="text-center mb-12">
                    <h1 className="text-4xl font-bold mb-2">로드맵 미리보기</h1>
                    <p className="text-gray-600">AI가 만들어준 로드맵을 미리 확인해 볼 수 있어요</p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {/* 첫 번째 줄: 3개의 카드 */}
                    <div className="col-span-3 grid grid-cols-3 gap-6">
                        {roadmapSamples.slice(0, 3).map((roadmap, index) => (
                            <RoadmapCard key={index} {...roadmap} />
                        ))}
                    </div>

                    {/* 두 번째 줄: 2개의 카드와 버튼 */}
                    <div className="col-span-3 grid grid-cols-3 gap-6">
                        {/* 2개의 카드 */}
                        {roadmapSamples.slice(3, 5).map((roadmap, index) => (
                            <RoadmapCard key={index + 3} {...roadmap} />
                        ))}

                        {/* 버튼 섹션 */}
                        <div className="flex flex-col justify-center items-center">
                            <p className="text-gray-600 mb-4">맞춤 로드맵을 만들고 싶다면?</p>
                            <button className="group px-6 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition-all duration-300 flex items-center gap-2">
                                나만의 로드맵 생성하기
                                <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RoadmapPreview;