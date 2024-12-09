import axios from 'axios';

export const BASE_URL = import.meta.env.VITE_API_URL || 'https://api.moyastudy.com';
export const WS_URL = import.meta.env.VITE_WS_URL || 'wss://api.moyastudy.com/ws-stomp';

export interface AuthResponse {
    token: string;
    memberInfo?: {
        id: number;
        email: string;
        nickname: string;
        profileImage?: string;
    };
}

export const API_ENDPOINTS = {
    AUTH: {
        GOOGLE_LOGIN: '/oauth2/authorization/google',  // 수정
        GOOGLE_CALLBACK: '/login/oauth2/code/google',  // 수정
        SIGNUP_COMPLETE: '/api/auth/oauth/signup/complete',
        CHECK_NICKNAME: (nickname: string) => `/api/auth/check-nickname/${nickname}`,
        LOGOUT: '/api/auth/logout',
        REFRESH: '/api/auth/refresh',
    },
} as const;

// axios 인스턴스 생성
export const axiosInstance = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: true,
});

// 인증 관련 API
export const AUTH_API = {

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
