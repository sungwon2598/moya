// src/config/apiConfig.ts
import axios, { AxiosError } from 'axios';
import { ChatRoomInfo, CreateRoomRequest } from '@/types/chat';

export const BASE_URL = import.meta.env.VITE_API_URL || 'https://api.moyastudy.com';
export const WS_URL = import.meta.env.VITE_WS_URL || 'wss://api.moyastudy.com/ws-stomp';

// Types
export interface SignUpFormData {
    nickname: string;
    token: string;
    tokenExpirationTime?: string;
    termsAgreed: boolean;
    privacyPolicyAgreed: boolean;
    marketingAgreed: boolean;
}

export interface AuthResponse {
    token: string;
    memberInfo?: {
        id: number;
        email: string;
        nickname: string;
        profileImage?: string;
    };
}

export interface OAuthCallbackResponse {
    accessToken: string;
    refreshToken: string | null;
    nextStep: 'LOGIN' | 'SIGNUP';
    memberInfo?: {
        id: number;
        email: string;
        nickname: string;
        profileImage?: string;
    };
}

// axios 인스턴스 생성
export const axiosInstance = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: true,
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

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
    AUTH: {
        GOOGLE_LOGIN: '/oauth2/authorization/google',  // 수정
        GOOGLE_CALLBACK: '/login/oauth2/code/google',  // 수정
        SIGNUP_COMPLETE: '/api/auth/oauth/signup/complete',
        CHECK_NICKNAME: (nickname: string) => `/api/auth/check-nickname/${nickname}`,
        LOGOUT: '/api/auth/logout',
        REFRESH: '/api/auth/refresh',
    },
} as const;

// 채팅 API 메서드
export const CHAT_API = {
    getAllRooms: async () => {
        try {
            const response = await axiosInstance.get<never, ChatRoomInfo[]>(API_ENDPOINTS.CHAT.ROOMS);
            return response;
        } catch (error) {
            console.error('Failed to fetch rooms:', error);
            throw error;
        }
    },

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

// 인증 관련 API
export const AUTH_API = {

    handleOAuthCallback: async (code: string): Promise<OAuthCallbackResponse> => {
        try {
            const response = await axiosInstance.get<never, OAuthCallbackResponse>(
                `${API_ENDPOINTS.AUTH.GOOGLE_CALLBACK}?code=${code}`
            );
            return response;
        } catch (error) {
            console.error('Google callback failed:', error);
            throw error;
        }
    },

    signUpComplete: async (formData: SignUpFormData): Promise<AuthResponse> => {
        try {
            const response = await axiosInstance.post<never, AuthResponse>(
                API_ENDPOINTS.AUTH.SIGNUP_COMPLETE,
                formData
            );
            if (response.token) {
                localStorage.setItem('token', response.token);
            }
            return response;
        } catch (error) {
            console.error('Signup failed:', error);
            throw error;
        }
    },

    checkNickname: async (nickname: string): Promise<boolean> => {
        try {
            const response = await axiosInstance.get<never, { available: boolean }>(
                API_ENDPOINTS.AUTH.CHECK_NICKNAME(nickname)
            );
            return response.available;
        } catch (error) {
            console.error('Nickname check failed:', error);
            throw error;
        }
    },

    logout: async () => {
        try {
            await axiosInstance.post(API_ENDPOINTS.AUTH.LOGOUT);
            localStorage.removeItem('token');
        } catch (error) {
            console.error('Logout failed:', error);
            throw error;
        }
    },

    refreshToken: async () => {
        try {
            const response = await axiosInstance.post<never, AuthResponse>(API_ENDPOINTS.AUTH.REFRESH);
            if (response.token) {
                localStorage.setItem('token', response.token);
            }
            return response;
        } catch (error) {
            console.error('Token refresh failed:', error);
            throw error;
        }
    },
} as const;

export const handleApiError = (error: any) => {
    if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError;
        if (axiosError.response) {
            return {
                status: axiosError.response.status,
                message: (axiosError.response.data as any)?.message || '서버 에러가 발생했습니다.',
            };
        } else if (axiosError.request) {
            return {
                status: 0,
                message: '서버에서 응답이 없습니다.',
            };
        }
    }
    return {
        status: 500,
        message: '알 수 없는 에러가 발생했습니다.',
    };
};
