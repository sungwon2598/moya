import React from 'react'
import MainContent from './MainContent.tsx'
import RoadmapPreview from "@features/roadmap/RoadmapPreview.tsx";
import HotStudy from "@pages/hot-study/HotStudy.tsx";

const Main: React.FC = () => {

    return (
        <div className="min-h-screen bg-gray-50">
            <MainContent/>
            <RoadmapPreview/>
            <HotStudy/>
        </div>
    )
}

export default Main