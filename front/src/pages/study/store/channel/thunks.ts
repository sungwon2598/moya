import { createAsyncThunk } from '@reduxjs/toolkit';
import type { Channel } from '../../components/shared/types/channel';
import { setChannels, setCurrentChannel, setLoading, setError } from './slice';

export const fetchChannels = createAsyncThunk(
    'channel/fetchChannels',
    async (_, { dispatch }) => {
        try {
            dispatch(setLoading(true));
            // API 호출 로직 추가 예정
            const mockChannels: Channel[] = [
                { id: '1', name: '일반' },
                { id: '2', name: '공지사항' },
            ];
            dispatch(setChannels(mockChannels));
            return mockChannels;
        } catch (error) {
            dispatch(setError(error instanceof Error ? error.message : 'An error occurred'));
            throw error;
        } finally {
            dispatch(setLoading(false));
        }
    }
);
