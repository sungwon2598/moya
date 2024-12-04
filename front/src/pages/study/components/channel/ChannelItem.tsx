import * as React from 'react';
import { MessageSquare } from 'lucide-react';
import type { Channel } from '../shared/types/channel';

interface ChannelItemProps {
    channel: Channel;
    onClick: () => void;
}

const ChannelItem: React.FC<ChannelItemProps> = ({ channel, onClick }) => {
    return (
        <button
            className="w-full text-left p-2 rounded-lg hover:bg-gray-100 group"
            onClick={onClick}
        >
            <div className="flex items-center space-x-3">
                <MessageSquare className="w-5 h-5 text-gray-500 group-hover:text-gray-700" />
                <span className="text-gray-700 group-hover:text-gray-900">{channel.name}</span>
            </div>
        </button>
    );
};

export default ChannelItem;
