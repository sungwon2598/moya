import React from 'react';
import ChannelItem from './ChannelItem';
import type { Channel } from '../shared/types/channel';

interface ChannelListProps {
    channels: Channel[];
    onSelectChannel: (channel: Channel) => void;
}

const ChannelList: React.FC<ChannelListProps> = ({ channels, onSelectChannel }) => {
    return (
        <div className="flex-1 overflow-y-auto">
            <div className="p-2">
                {channels.map((channel) => (
                    <ChannelItem
                        key={channel.id}
                        channel={channel}
                        onClick={() => onSelectChannel(channel)}
                    />
                ))}
            </div>
        </div>
    );
};

export default ChannelList;
