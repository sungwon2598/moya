import { useSlideControl } from '@shared/hooks/useSlideControl.ts';
import HotStudyCard from './HotStudyCard';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import React, { useEffect, useState } from 'react';
import { studyApiService, HotPost } from '@/core/config/studyApiConfig.ts';

const HotStudy: React.FC = () => {
  const [posts, setPosts] = useState<HotPost[]>([]);

  const { currentPage, maxPages, visibleItems, handlePrevClick, handleNextClick } = useSlideControl(posts, 5);

  const fetchPosts = async () => {
    try {
      const response = await studyApiService.getHotStudyList();
      console.log(response);
      setPosts(response.data.slice(0, 10));
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  return (
    <div className="max-w-6xl px-4 py-8 mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-black">🔥 Hot 스터디</h2>
        <div className="flex gap-2">
          <button
            className={`rounded-full p-2 transition-all ${
              currentPage === 0 ? 'cursor-not-allowed opacity-50' : 'hover:bg-gray-100'
            }`}
            onClick={handlePrevClick}
            disabled={currentPage === 0}>
            <ChevronLeft className="w-6 h-6" />
          </button>
          <button
            className={`rounded-full p-2 transition-all ${
              currentPage === maxPages ? 'cursor-not-allowed opacity-50' : 'hover:bg-gray-100'
            }`}
            onClick={handleNextClick}
            disabled={currentPage === maxPages}>
            <ChevronRight className="w-6 h-6" />
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-5">
        {visibleItems.map((card, index) => (
          <HotStudyCard key={index} card={card} />
        ))}
      </div>
    </div>
  );
};

export default HotStudy;
