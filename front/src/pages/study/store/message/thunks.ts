import { createAsyncThunk } from '@reduxjs/toolkit';
import type { Message } from '../../components/shared/types/message';
import { setMessages, addMessage, setLoading, setError } from './slice';

export const fetchMessages = createAsyncThunk(
    'message/fetchMessages',
    async (channelId: string, { dispatch }) => {
        try {
            dispatch(setLoading(true));
            // API 호출 로직 추가 예정
            const mockMessages: Message[] = [
                {
                    id: '1',
                    content: '안녕하세요!',
                    sender: '사용자1',
                    timestamp: new Date(),
                },
            ];
            dispatch(setMessages({ channelId, messages: mockMessages }));
            return mockMessages;
        } catch (error) {
            dispatch(setError(error instanceof Error ? error.message : 'An error occurred'));
            throw error;
        } finally {
            dispatch(setLoading(false));
        }
    }
);

export const sendMessage = createAsyncThunk(
    'message/sendMessage',
    async ({ channelId, content }: { channelId: string; content: string }, { dispatch }) => {
        try {
            // API 호출 로직 추가 예정
            const newMessage: Message = {
                id: Date.now().toString(),
                content,
                sender: '현재 사용자',
                timestamp: new Date(),
            };
            dispatch(addMessage({ channelId, message: newMessage }));
            return newMessage;
        } catch (error) {
            dispatch(setError(error instanceof Error ? error.message : 'An error occurred'));
            throw error;
        }
    }
);
