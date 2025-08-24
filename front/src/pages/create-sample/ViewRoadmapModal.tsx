import React, { useState } from 'react';
import type { WeeklyRoadmapResponse } from '@/types/roadmap';
import { ChevronLeft, ChevronRight } from 'lucide-react';

interface ViewRoadmapModalProps {
  roadmapResponse: WeeklyRoadmapResponse | null;
}

const ViewRoadmapModal: React.FC<ViewRoadmapModalProps> = ({ roadmapResponse }) => {
  const [currentWeek, setCurrentWeek] = useState(1);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  if (!roadmapResponse) return null;

  const currentWeekData = roadmapResponse.weeklyPlans.find((week) => week.week === currentWeek);
  const totalWeeks = roadmapResponse.weeklyPlans.length;
  const otherWeeks = Array.from({ length: totalWeeks }, (_, i) => i + 1).filter((week) => week !== currentWeek);

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
    <div className="mx-auto max-h-[80vh] max-w-2xl overflow-y-auto p-4">
      {/* Week Navigation */}
      <div className="mb-4 flex items-center justify-between">
        <button
          onClick={handlePrevWeek}
          disabled={currentWeek === 1}
          className={`rounded-full p-1 transition-colors ${
            currentWeek === 1 ? 'cursor-not-allowed text-gray-300' : 'text-gray-600 hover:bg-gray-100'
          }`}>
          <ChevronLeft size={20} />
        </button>

        {/* Week Dropdown */}
        <div className="relative">
          <button
            onClick={() => setIsDropdownOpen(!isDropdownOpen)}
            className="rounded-md px-3 py-1 text-lg font-bold text-gray-900 transition-colors hover:bg-gray-100">
            {currentWeek}주차
          </button>

          {isDropdownOpen && (
            <>
              <div className="fixed inset-0 z-10" onClick={() => setIsDropdownOpen(false)}></div>
              <div className="absolute z-20 mt-2 w-32 rounded-md bg-white py-2 shadow-lg">
                {otherWeeks.map((week) => (
                  <button
                    key={week}
                    onClick={() => {
                      setCurrentWeek(week);
                      setIsDropdownOpen(false);
                    }}
                    className="w-full px-4 py-2 text-left text-gray-700 hover:bg-gray-100">
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
          className={`rounded-full p-1 transition-colors ${
            currentWeek === totalWeeks ? 'cursor-not-allowed text-gray-300' : 'text-gray-600 hover:bg-gray-100'
          }`}>
          <ChevronRight size={20} />
        </button>
      </div>

      {/* Weekly Keyword */}
      <h3 className="mb-6 text-xl font-bold text-gray-800">{currentWeekData?.weeklyKeyword}</h3>

      {/* Daily Plans */}
      <div className="relative mt-8">
        {currentWeekData?.dailyPlans.map((plan, index) => (
          <div key={plan.day} className="relative mb-8 last:mb-0">
            <div className="flex items-center">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-500 text-white">
                <span className="text-sm">{plan.day}</span>
              </div>
              <div className="ml-3">
                <h3 className="text-base font-medium text-gray-900">{plan.dailyKeyword}</h3>
              </div>
            </div>
            {index < 6 && <div className="absolute left-4 top-8 h-8 w-0.5 bg-gray-200"></div>}
          </div>
        ))}
      </div>

      {/* Tips and Evaluation */}
      <div className="mt-6 rounded-lg bg-gray-50 p-3">
        <h4 className="mb-2 text-base font-semibold">학습 팁</h4>
        <ul className="mb-3 list-disc pl-4 text-sm">
          {roadmapResponse.overallTips.map((tip, index) => (
            <li key={index} className="mb-1 text-gray-700">
              {tip}
            </li>
          ))}
        </ul>
        <div className="mt-3">
          <h4 className="mb-2 text-base font-semibold">커리큘럼 평가</h4>
          <p className="text-sm text-gray-700">{roadmapResponse.curriculumEvaluation}</p>
        </div>
      </div>
    </div>
  );
};

export default ViewRoadmapModal;
