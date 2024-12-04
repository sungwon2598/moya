import React from 'react';
import Sidebar from './Sidebar';
import ChatArea from './ChatArea';

const StudyLayout: React.FC = () => {
    return (
        <div className="w-full h-[calc(100vh-48px)] flex">
            <Sidebar />
            <ChatArea />
        </div>
    );
};

export default StudyLayout;
