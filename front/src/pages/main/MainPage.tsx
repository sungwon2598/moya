import React from 'react'
import MainContent from './MainContent.tsx'
import RoadmapPreview from "@features/roadmap/RoadmapPreview.tsx";

const Main: React.FC = () => {

    return (
        <div className="min-h-screen bg-gray-50">
            <MainContent/>
            <RoadmapPreview/>
        </div>
    )
}

export default Main