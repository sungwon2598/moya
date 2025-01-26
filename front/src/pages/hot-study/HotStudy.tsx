import { studyCardsData } from './hotStudyMockData.ts';
import { useSlideControl } from '@shared/hooks/useSlideControl.ts';
import HotStudyCard from './HotStudyCard';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import React from "react";

const HotStudy: React.FC = () => {
    const {
        currentPage,
        maxPages,
        visibleItems,
        handlePrevClick,
        handleNextClick
    } = useSlideControl(studyCardsData, 4);

    return (
        <div className="max-w-6xl mx-auto px-4 py-8">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-black">🔥 Hot 스터디</h2>
                <div className="flex gap-2">
                    <button
                        className={`p-2 rounded-full transition-all ${
                            currentPage === 0 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-100'
                        }`}
                        onClick={handlePrevClick}
                        disabled={currentPage === 0}
                    >
                        <ChevronLeft className="w-6 h-6"/>
                    </button>
                    <button
                        className={`p-2 rounded-full transition-all ${
                            currentPage === maxPages ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-100'
                        }`}
                        onClick={handleNextClick}
                        disabled={currentPage === maxPages}
                    >
                        <ChevronRight className="w-6 h-6"/>
                    </button>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                {visibleItems.map((card, index) => (
                    <HotStudyCard key={index} card={card} />
                ))}
            </div>
        </div>
    );
};

export default HotStudy;