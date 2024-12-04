import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { Message } from '../../components/shared/types/message';

interface MessageState {
    messages: Record<string, Message[]>; // channelId를 key로 사용
    loading: boolean;
    error: string | null;
}

const initialState: MessageState = {
    messages: {},
    loading: false,
    error: null,
};

const messageSlice = createSlice({
    name: 'message',
    initialState,
    reducers: {
        setMessages: (state, action: PayloadAction<{ channelId: string; messages: Message[] }>) => {
            const { channelId, messages } = action.payload;
            state.messages[channelId] = messages;
        },
        addMessage: (state, action: PayloadAction<{ channelId: string; message: Message }>) => {
            const { channelId, message } = action.payload;
            if (!state.messages[channelId]) {
                state.messages[channelId] = [];
            }
            state.messages[channelId].push(message);
        },
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setError: (state, action: PayloadAction<string | null>) => {
            state.error = action.payload;
        },
    },
});

export const { setMessages, addMessage, setLoading, setError } = messageSlice.actions;
export default messageSlice.reducer;
