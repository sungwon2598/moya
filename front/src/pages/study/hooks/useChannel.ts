import { useState, useCallback } from 'react';
import type { Channel } from '../components/shared/types/channel';

export const useChannel = () => {
    const [channels, setChannels] = useState<Channel[]>([
        { id: '1', name: '일반' }
    ]);
    const [currentChannel, setCurrentChannel] = useState<Channel | null>(null);

    const selectChannel = useCallback((channel: Channel) => {
        setCurrentChannel(channel);
    }, []);

    return { channels, currentChannel, selectChannel };
};
