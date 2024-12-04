import { configureStore } from '@reduxjs/toolkit';
import channelReducer from './channel/slice';
import messageReducer from './message/slice';

export const store = configureStore({
    reducer: {
        channel: channelReducer,
        message: messageReducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {
                // timestamp가 Date 객체이므로 직렬화 검사 제외
                ignoredPaths: ['message.messages'],
            },
        }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
