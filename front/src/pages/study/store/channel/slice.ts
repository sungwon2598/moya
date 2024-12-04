import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { Channel } from '../../components/shared/types/channel';

interface ChannelState {
    channels: Channel[];
    currentChannel: Channel | null;
    loading: boolean;
    error: string | null;
}

const initialState: ChannelState = {
    channels: [],
    currentChannel: null,
    loading: false,
    error: null,
};

const channelSlice = createSlice({
    name: 'channel',
    initialState,
    reducers: {
        setChannels: (state, action: PayloadAction<Channel[]>) => {
            state.channels = action.payload;
        },
        setCurrentChannel: (state, action: PayloadAction<Channel>) => {
            state.currentChannel = action.payload;
        },
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setError: (state, action: PayloadAction<string | null>) => {
            state.error = action.payload;
        },
    },
});
export const { setChannels, setCurrentChannel, setLoading, setError } = channelSlice.actions;
export default channelSlice.reducer;
