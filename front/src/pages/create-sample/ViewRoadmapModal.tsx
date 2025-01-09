import React, { useState } from "react";
import { WeeklyRoadmapResponse } from "@core/config/roadmapApiConfig";
import { ChevronLeft, ChevronRight } from "lucide-react";

interface ViewRoadmapModalProps {
    roadmapResponse: WeeklyRoadmapResponse | null;
}

const ViewRoadmapModal: React.FC<ViewRoadmapModalProps> = ({ roadmapResponse }) => {
    const [currentWeek, setCurrentWeek] = useState(1);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);

    if (!roadmapResponse) return null;

    const currentWeekData = roadmapResponse.weeklyPlans.find(week => week.week === currentWeek);
    const totalWeeks = roadmapResponse.weeklyPlans.length;
    const otherWeeks = Array.from({ length: totalWeeks }, (_, i) => i + 1)
        .filter(week => week !== currentWeek);

    const handlePrevWeek = () => {
        if (currentWeek > 1) {
            setCurrentWeek(currentWeek - 1);
        }
    };

    const handleNextWeek = () => {
        if (currentWeek < totalWeeks) {
            setCurrentWeek(currentWeek + 1);
        }
    };

    return (
        <div className="p-4 max-w-2xl mx-auto max-h-[80vh] overflow-y-auto">
            {/* Week Navigation */}
            <div className="flex justify-between items-center mb-4">
                <button
                    onClick={handlePrevWeek}
                    disabled={currentWeek === 1}
                    className={`p-1 rounded-full transition-colors ${
                        currentWeek === 1
                            ? 'text-gray-300 cursor-not-allowed'
                            : 'text-gray-600 hover:bg-gray-100'
                    }`}
                >
                    <ChevronLeft size={20} />
                </button>

                {/* Week Dropdown */}
                <div className="relative">
                    <button
                        onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                        className="text-lg font-bold text-gray-900 px-3 py-1 hover:bg-gray-100 rounded-md transition-colors"
                    >
                        {currentWeek}주차
                    </button>

                    {isDropdownOpen && (
                        <>
                            <div
                                className="fixed inset-0 z-10"
                                onClick={() => setIsDropdownOpen(false)}
                            ></div>
                            <div className="absolute mt-2 py-2 w-32 bg-white rounded-md shadow-lg z-20">
                                {otherWeeks.map((week) => (
                                    <button
                                        key={week}
                                        onClick={() => {
                                            setCurrentWeek(week);
                                            setIsDropdownOpen(false);
                                        }}
                                        className="w-full px-4 py-2 text-left hover:bg-gray-100 text-gray-700"
                                    >
                                        {week}주차
                                    </button>
                                ))}
                            </div>
                        </>
                    )}
                </div>

                <button
                    onClick={handleNextWeek}
                    disabled={currentWeek === totalWeeks}
                    className={`p-1 rounded-full transition-colors ${
                        currentWeek === totalWeeks
                            ? 'text-gray-300 cursor-not-allowed'
                            : 'text-gray-600 hover:bg-gray-100'
                    }`}
                >
                    <ChevronRight size={20} />
                </button>
            </div>

            {/* Weekly Keyword */}
            <h3 className="text-xl font-bold text-gray-800 mb-6">
                {currentWeekData?.weeklyKeyword}
            </h3>

            {/* Daily Plans */}
            <div className="relative mt-8">
                {currentWeekData?.dailyPlans.map((plan, index) => (
                    <div key={plan.day} className="relative mb-8 last:mb-0">
                        <div className="flex items-center">
                            <div className="w-8 h-8 rounded-full flex items-center justify-center bg-blue-500 text-white">
                                <span className="text-sm">{plan.day}</span>
                            </div>
                            <div className="ml-3">
                                <h3 className="text-base text-gray-900 font-medium">
                                    {plan.dailyKeyword}
                                </h3>
                            </div>
                        </div>
                        {index < 6 && (
                            <div className="absolute left-4 top-8 w-0.5 h-8 bg-gray-200"></div>
                        )}
                    </div>
                ))}
            </div>

            {/* Tips and Evaluation */}
            <div className="mt-6 p-3 bg-gray-50 rounded-lg">
                <h4 className="text-base font-semibold mb-2">학습 팁</h4>
                <ul className="list-disc pl-4 mb-3 text-sm">
                    {roadmapResponse.overallTips.map((tip, index) => (
                        <li key={index} className="text-gray-700 mb-1">{tip}</li>
                    ))}
                </ul>
                <div className="mt-3">
                    <h4 className="text-base font-semibold mb-2">커리큘럼 평가</h4>
                    <p className="text-sm text-gray-700">{roadmapResponse.curriculumEvaluation}</p>
                </div>
            </div>
        </div>
    );
};

export default ViewRoadmapModal;