import React from 'react';
import MainContent from './MainContent.tsx';
import RoadmapPreview from '@/components/features/main/RoadmapPreview.tsx';
import HotStudy from '@pages/hot-study/HotStudy.tsx';

const Main: React.FC = () => {
  return (
    <div className="min-h-screen overflow-hidden">
      <MainContent />
      <RoadmapPreview />
      <HotStudy />
    </div>
  );
};

export default Main;
