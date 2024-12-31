import React, { useEffect, useRef } from 'react';
import Masonry from 'masonry-layout'; // Masonry 라이브러리 추가
import StatsSection from "@pages/adminator/components/dashboard/components/state-section/StateSection.tsx";
import Header from "@pages/adminator/layout/header/Header";
import Footer from "@pages/adminator/layout/Footer";
import "@pages/adminator/assets/styles/index.scss";

const Dashboard: React.FC = () => {
    const masonryRef = useRef<HTMLDivElement>(null); // Masonry 컨테이너 참조

    // Masonry 초기화
    useEffect(() => {
        if (masonryRef.current) {
            new Masonry(masonryRef.current, {
                itemSelector: '.masonry-item', // 아이템 선택자
                columnWidth: '.masonry-sizer', // 컬럼 크기
                percentPosition: true, // 반응형 지원
            });
        }
    }, []); // 컴포넌트가 마운트될 때만 실행

    return (
        <div className="page-container">
            {/* Header */}
            <Header />

            {/* Main Content */}
            <main className='main-content bgc-grey-100'>
                <div id='mainContent'>
                    {/* Masonry Container */}
                    {/*<div ref={masonryRef} className="row gap-20 masonry pos-r">*/}
                    <div ref={masonryRef} className="row gap-20 masonry pos-r">
                        {/* Masonry sizer */}
                        <div className="masonry-sizer col-md-6"></div>

                        {/* Stats Cards Section */}
                        <div className="masonry-item w-100">
                            <StatsSection />
                        </div>

                        {/* Site Visits Section - 추후 추가 */}
                        {/* Monthly Stats & Todo List Section - 추후 추가 */}
                        {/* Sales Report & Weather Section - 추후 추가 */}
                        {/* Chat Section - 추후 추가 */}
                    </div>
                </div>
            </main>

            {/* Footer */}
            <Footer />
        </div>
    );
};

export default Dashboard;
