import { calculateDaysLeft } from '@shared/utils/dateUtils.ts';
import React from 'react';
import { HotPost } from '@/core/config/studyApiConfig.ts';
import { useNavigate } from 'react-router-dom';

interface HotStudyCardProps {
  card: HotPost;
}

const HotStudyCard: React.FC<HotStudyCardProps> = ({ card }) => {
  const end = new Date(card.endDate);

  const daysLeft = calculateDaysLeft(card.endDate);
  const isExpired = daysLeft < 0;
  const navigate = useNavigate();

  return (
    <div
      className="relative transition-all duration-300 bg-white border-2 border-gray-200 rounded-lg hover:border-moya-primary group h-44 hover:-translate-y-1 hover:shadow-lg"
      onClick={() => navigate(`/study/${card.postId}`)}>
      <div className="relative h-full p-3">
        <div className="absolute left-3 top-3">
          {/* {card.tags.map((tag, tagIndex) => (
                        <span key={tagIndex} className="inline-flex px-2 py-1 text-xs font-bold leading-none text-gray-500 border border-gray-200 rounded-full">
                            {tag}
                        </span>
                    ))} */}
        </div>
        {isExpired ? (
          <span className="absolute inline-flex px-2 py-1 text-xs font-bold leading-none text-gray-500 border border-gray-500 rounded-full right-3 top-3">
            ⌛ 마감됨 ({Math.abs(daysLeft)}일 전)
          </span>
        ) : (
          <span className="absolute inline-flex px-2 py-1 text-xs font-bold leading-none text-red-500 border border-red-500 rounded-full right-3 top-3">
            🔥 마감 {daysLeft}일 남음
          </span>
        )}

        <div className="pt-10">
          <div className="mb-1 text-sm text-gray-500">마감일 | {end.toLocaleDateString()}</div>
          <h3 className="text-lg font-semibold line-clamp-1">{card.title}</h3>
        </div>

        <div className="absolute flex items-center text-sm text-gray-500 bottom-3 right-3">
          <span className="flex items-center">👀 조회수 {card.views}회</span>
        </div>
      </div>
    </div>
  );
};

export default HotStudyCard;
