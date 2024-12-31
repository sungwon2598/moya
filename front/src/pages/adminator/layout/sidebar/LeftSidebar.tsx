import React from 'react';
import SidebarHeader from './SidebarHeader.tsx';
import SidebarMenu from './SidebarMenu.tsx';
import '../../assets/styles/index.scss';

const LeftSidebar: React.FC = () => {
    return (
        <div className="sidebar">
            <div className="sidebar-inner">
                <SidebarHeader />
                <SidebarMenu />
            </div>
        </div>
    );
};

export default LeftSidebar;
