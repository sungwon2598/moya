import axios, { AxiosInstance, AxiosError } from 'axios';

export const BASE_URL = import.meta.env.VITE_API_URL || 'https://api.moyastudy.com';
export const WS_URL = import.meta.env.VITE_WS_URL || 'wss://api.moyastudy.com/ws-stomp';

// Types
export interface AuthResponse {
    token: string;
    memberInfo?: {
        id: number;
        email: string;
        nickname: string;
        profileImage?: string;
    };
}

export interface ApiResponse<T> {
    success: boolean;
    data?: T;
    message?: string;
}

// API Endpoints
export const API_ENDPOINTS = {
    AUTH: {
        GOOGLE_LOGIN: '/oauth2/authorization/google',
        GOOGLE_CALLBACK: '/login/oauth2/code/google',
        SIGNUP_COMPLETE: '/api/auth/oauth/signup/complete',
        CHECK_NICKNAME: (nickname: string) => `/api/auth/check-nickname/${nickname}`,
        LOGOUT: '/api/auth/logout',
        REFRESH: '/api/auth/refresh',
    },
} as const;

// Axios instance creation with interceptors
export const createAxiosInstance = (): AxiosInstance => {
    const instance = axios.create({
        baseURL: BASE_URL,
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        withCredentials: true,
    });

    // Request Interceptor
    instance.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem('token');
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        },
        (error) => {
            console.error('[API] Request interceptor error:', error);
            return Promise.reject(error);
        }
    );

    // Response Interceptor
    instance.interceptors.response.use(
        (response) => response,
        async (error: AxiosError) => {
            const originalRequest = error.config;

            if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
                originalRequest._retry = true;

                try {
                    const newToken = await AUTH_API.refreshToken();
                    if (originalRequest.headers) {
                        originalRequest.headers.Authorization = `Bearer ${newToken.token}`;
                    }
                    return instance(originalRequest);
                } catch (refreshError) {
                    // 토큰 갱신 실패 시 로그아웃 처리
                    await AUTH_API.logout();
                    window.location.href = '/login';
                    return Promise.reject(refreshError);
                }
            }

            return Promise.reject(error);
        }
    );

    return instance;
};

// Create and export axios instance
export const axiosInstance = createAxiosInstance();

// Auth API methods
export const AUTH_API = {
    logout: async (): Promise<void> => {
        try {
            await axiosInstance.post(API_ENDPOINTS.AUTH.LOGOUT);
            localStorage.removeItem('token');
        } catch (error) {
            console.error('[Auth API] Logout failed:', error);
            localStorage.removeItem('token'); // 에러가 발생해도 토큰 제거
            throw error;
        }
    },

    refreshToken: async (): Promise<AuthResponse> => {
        try {
            const { data } = await axiosInstance.post<AuthResponse>(
                API_ENDPOINTS.AUTH.REFRESH
            );

            if (data.token) {
                localStorage.setItem('token', data.token);
            }

            return data;
        } catch (error) {
            console.error('[Auth API] Token refresh failed:', error);
            throw error;
        }
    },

    checkNickname: async (nickname: string): Promise<boolean> => {
        try {
            const { data } = await axiosInstance.get(
                API_ENDPOINTS.AUTH.CHECK_NICKNAME(nickname)
            );
            return data.available;
        } catch (error) {
            console.error('[Auth API] Nickname check failed:', error);
            throw error;
        }
    }
} as const;

// Type declaration for axios config
declare module 'axios' {
    export interface AxiosRequestConfig {
        _retry?: boolean;
    }
}
