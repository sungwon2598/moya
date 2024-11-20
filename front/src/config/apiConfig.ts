// src/config/apiConfig.ts
import axios, { AxiosError } from 'axios';
import { ChatRoomInfo, CreateRoomRequest } from '@/types/chat';

// API 기본 URL 설정
export const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
export const WS_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8080/ws-stomp';

// axios 인스턴스 생성
export const axiosInstance = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: true, // CORS credentials 설정
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
    (config) => {
        // 개발 환경에서 디버깅
        if (process.env.NODE_ENV === 'development') {
            console.log('API Request:', config);
        }

        return config;
    },
    (error: AxiosError) => {
        console.error('Request Error:', error);
        return Promise.reject(error);
    }
);

// 응답 인터셉터
axiosInstance.interceptors.response.use(
    (response) => {
        // 개발 환경에서 디버깅
        if (process.env.NODE_ENV === 'development') {
            console.log('API Response:', response);
        }
        return response.data;
    },
    (error: AxiosError) => {
        if (error.response) {
            console.error('API Error:', {
                status: error.response.status,
                data: error.response.data,
                headers: error.response.headers,
            });
        } else if (error.request) {
            console.error('No response received:', error.request);
        } else {
            console.error('Request error:', error.message);
        }

        return Promise.reject(error);
    }
);

// API 엔드포인트 정의
export const API_ENDPOINTS = {
    CHAT: {
        ROOMS: '/ws/chat',
        CREATE_ROOM: '/ws/chat/create',
        ROOM_INFO: (roomId: string) => `/ws/chat/room/${roomId}`,
    },
    /* 인증 관련 엔드포인트는 주석 처리
    AUTH: {
        LOGIN: '/auth/login',
        LOGOUT: '/auth/logout',
        REFRESH: '/auth/refresh',
    },
    USER: {
        PROFILE: '/user/profile',
        UPDATE: '/user/update',
    },
    */
} as const;

// 채팅 API 메서드
export const CHAT_API = {
    // 채팅방 목록 조회
    getAllRooms: async () => {
        try {
            const response = await axiosInstance.get<never, ChatRoomInfo[]>(API_ENDPOINTS.CHAT.ROOMS);
            return response;
        } catch (error) {
            console.error('Failed to fetch rooms:', error);
            throw error;
        }
    },

    // 새로운 채팅방 생성
    createRoom: async (data: CreateRoomRequest) => {
        try {
            const response = await axiosInstance.post<never, ChatRoomInfo>(
                API_ENDPOINTS.CHAT.CREATE_ROOM,
                data
            );
            return response;
        } catch (error) {
            console.error('Failed to create room:', error);
            throw error;
        }
    },

    // 특정 채팅방 정보 조회
    getRoom: async (roomId: string) => {
        try {
            const response = await axiosInstance.get<never, ChatRoomInfo>(
                API_ENDPOINTS.CHAT.ROOM_INFO(roomId)
            );
            return response;
        } catch (error) {
            console.error('Failed to fetch room info:', error);
            throw error;
        }
    },
} as const;

/* 인증 관련 API
export const AUTH_API = {
    login: async (credentials: { username: string; password: string }) => {
        try {
            const response = await axiosInstance.post(API_ENDPOINTS.AUTH.LOGIN, credentials);
            if (response.accessToken) {
                localStorage.setItem('accessToken', response.accessToken);
            }
            return response;
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    },

    logout: async () => {
        try {
            await axiosInstance.post(API_ENDPOINTS.AUTH.LOGOUT);
            localStorage.removeItem('accessToken');
        } catch (error) {
            console.error('Logout failed:', error);
            throw error;
        }
    },

    refreshToken: async () => {
        try {
            const response = await axiosInstance.post(API_ENDPOINTS.AUTH.REFRESH);
            if (response.accessToken) {
                localStorage.setItem('accessToken', response.accessToken);
            }
            return response;
        } catch (error) {
            console.error('Token refresh failed:', error);
            throw error;
        }
    },
} as const;

export const USER_API = {
    getProfile: async () => {
        try {
            const response = await axiosInstance.get(API_ENDPOINTS.USER.PROFILE);
            return response;
        } catch (error) {
            console.error('Failed to fetch user profile:', error);
            throw error;
        }
    },

    updateProfile: async (data: any) => {
        try {
            const response = await axiosInstance.put(API_ENDPOINTS.USER.UPDATE, data);
            return response;
        } catch (error) {
            console.error('Failed to update user profile:', error);
            throw error;
        }
    },
} as const;
*/

// 에러 처리 유틸리티
export const handleApiError = (error: any) => {
    if (axios.isAxiosError(error)) {
        // Axios 에러 처리
        const axiosError = error as AxiosError;
        if (axiosError.response) {
            // 서버 응답이 있는 경우
            return {
                status: axiosError.response.status,
                message: (axiosError.response.data as any)?.message || '서버 에러가 발생했습니다.',
            };
        } else if (axiosError.request) {
            // 요청은 보냈지만 응답을 받지 못한 경우
            return {
                status: 0,
                message: '서버에서 응답이 없습니다.',
            };
        }
    }
    // 기타 에러
    return {
        status: 500,
        message: '알 수 없는 에러가 발생했습니다.',
    };
};

export default {
    CHAT_API,
    // AUTH_API,  // 주석 처리
    // USER_API,  // 주석 처리
    handleApiError,
    API_ENDPOINTS,
};