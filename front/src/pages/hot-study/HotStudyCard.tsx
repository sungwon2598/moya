import { StudyCard } from './hotStudyMockData.ts';
import { calculateDaysLeft } from '@shared/utils/dateUtils.ts';
import React from "react";

interface HotStudyCardProps {
    card: StudyCard;
}

const HotStudyCard: React.FC<HotStudyCardProps> = ({ card }) => (
    <div className="group relative bg-white rounded-lg border-2 border-gray-200 hover:border-red-500 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 h-44">
        <div className="p-3 h-full relative">
            <div className="absolute top-3 left-3">
                {card.tags.map((tag, tagIndex) => (
                    <span key={tagIndex} className="inline-flex px-2 py-1 text-xs rounded-full text-gray-500 border border-gray-200 font-bold leading-none">
                        {tag}
                    </span>
                ))}
            </div>

            <span className="absolute top-3 right-3 inline-flex px-2 py-1 text-xs rounded-full text-red-500 border border-red-500 font-bold leading-none">
                🚨 마감 {calculateDaysLeft(card.deadline)}일전
            </span>

            <div className="pt-10">
                <div className="text-sm text-gray-500 mb-1">
                    마감일 | {card.deadline}
                </div>
                <h3 className="text-lg font-semibold">{card.title}</h3>
            </div>

            <div className="absolute bottom-3 right-3 flex items-center text-sm text-gray-500">
                <span className="flex items-center">
                    👀 조회수 {card.viewCount}회
                </span>
            </div>
        </div>
    </div>
);

export default HotStudyCard;