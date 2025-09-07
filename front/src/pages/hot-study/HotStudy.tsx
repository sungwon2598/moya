import { useSlideControl } from '@shared/hooks/useSlideControl.ts';
import HotStudyCard from './HotStudyCard';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import React, { useEffect, useState } from 'react';
// import { studyApiService, HotPost } from '@/core/config/studyApiConfig.ts';
import { studyService } from '@/services/study';
import type { HotPost } from '@/types/study';

const HotStudy: React.FC = () => {
  const [posts, setPosts] = useState<HotPost[]>([]);
  const [isError, setIsError] = useState(false);

  const { currentPage, maxPages, visibleItems, handlePrevClick, handleNextClick } = useSlideControl(posts, 5);

  const fetchPosts = async () => {
    try {
      const response = await studyService.getHotStudyList();
      console.log(response);
      setPosts(response.data.slice(0, 10));
    } catch (error) {
      setIsError(true);
      console.log(error);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between">
        <h2 className="text-2xl font-bold">🔥 Hot 스터디</h2>

        <div className="flex gap-2">
          <button
            className={`aspect-square rounded-full border-2 p-2 transition-all ${
              currentPage === 0 ? 'cursor-not-allowed opacity-50' : 'hover:bg-gray-100'
            }`}
            onClick={handlePrevClick}
            disabled={currentPage === 0}>
            <ChevronLeft size={20} strokeWidth={3} />
          </button>
          <button
            className={`rounded-full border-2 p-2 transition-all ${
              currentPage === maxPages ? 'cursor-not-allowed opacity-50' : 'hover:bg-gray-100'
            }`}
            onClick={handleNextClick}
            disabled={currentPage === maxPages}>
            <ChevronRight size={20} strokeWidth={3} />
          </button>
        </div>
      </div>
      {isError ? (
        <div className="m-3 flex justify-center">
          <p>인기 게시글 조회에 실패 했습니다 😵‍💫</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-5">
          {visibleItems.map((card, index) => (
            <HotStudyCard key={index} card={card} />
          ))}
        </div>
      )}
    </div>
  );
};

export default HotStudy;
