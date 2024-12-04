import React from 'react';
import ChannelHeader from '../channel/ChannelHeader';
import ChannelList from '../channel/ChannelList';
import { useChannel } from '../../hooks/useChannel';

const Sidebar: React.FC = () => {
    const { channels, selectChannel } = useChannel();

    return (
        <div className="w-[300px] min-w-[300px] max-w-[300px] bg-gray-50 border-r border-gray-200">
            <div className="flex flex-col h-full">
                <ChannelHeader />
                <ChannelList channels={channels} onSelectChannel={selectChannel} />
            </div>
        </div>
    );
};

export default Sidebar;
