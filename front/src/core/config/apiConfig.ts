import axios, { AxiosInstance, AxiosError, AxiosRequestConfig } from 'axios';
import { createBrowserHistory } from 'history';

// Constants
export const BASE_URL = import.meta.env.VITE_API_URL || 'https://api.moyastudy.com';
export const WS_URL = import.meta.env.VITE_WS_URL || 'wss://api.moyastudy.com/ws-stomp';

const history = createBrowserHistory();

// Types
export interface MemberInfo {
    id: number;
    email: string;
    nickname: string;
    profileImage?: string;
}

export interface AuthResponse {
    token: string;
    memberInfo?: MemberInfo;
}

export interface ApiResponse<T> {
    success: boolean;
    data?: T;
    message?: string;
    errorCode?: string;
}

// API Endpoints
export const API_ENDPOINTS = {
    AUTH: {
        GOOGLE_LOGIN: '/oauth2/authorization/google',
        GOOGLE_CALLBACK: '/login/oauth2/code/google',
        SIGNUP_COMPLETE: '/api/auth/oauth/signup/complete',
        CHECK_NICKNAME: (nickname: string) => `/api/auth/check-nickname/${encodeURIComponent(nickname)}`,
        LOGOUT: '/api/auth/logout',
        REFRESH: '/api/auth/refresh',
    },
} as const;

// Custom error class
export class ApiError extends Error {
    constructor(
        message: string,
        public statusCode?: number,
        public errorCode?: string,
        public originalError?: Error
    ) {
        super(message);
        this.name = 'ApiError';
    }
}

// Token management
const TokenManager = {
    getToken: () => localStorage.getItem('token'),
    setToken: (token: string) => localStorage.setItem('token', token),
    removeToken: () => localStorage.removeItem('token'),

    isTokenExpired: (token: string): boolean => {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 < Date.now();
        } catch {
            return true;
        }
    }
};

// Request retry configuration
const MAX_RETRIES = 3;
const RETRY_DELAY = 1000;

const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// Axios instance creation with interceptors
export const createAxiosInstance = (): AxiosInstance => {
    const instance = axios.create({
        baseURL: BASE_URL,
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        withCredentials: true,
        timeout: 10000, // 10 seconds timeout
    });

    // Request Interceptor
    instance.interceptors.request.use(
        async (config) => {
            const token = TokenManager.getToken();
            if (token) {
                if (TokenManager.isTokenExpired(token) && config.url !== API_ENDPOINTS.AUTH.REFRESH) {
                    try {
                        const newToken = await AUTH_API.refreshToken();
                        TokenManager.setToken(newToken.token);
                        config.headers.Authorization = `Bearer ${newToken.token}`;
                    } catch (error) {
                        TokenManager.removeToken();
                        throw error;
                    }
                } else {
                    config.headers.Authorization = `Bearer ${token}`;
                }
            }
            return config;
        },
        (error) => {
            console.error('[API] Request interceptor error:', error);
            return Promise.reject(new ApiError('Request configuration failed', undefined, undefined, error));
        }
    );

    // Response Interceptor
    instance.interceptors.response.use(
        (response) => response,
        async (error: AxiosError) => {
            const originalRequest = error.config as AxiosRequestConfig & { _retry?: number };

            if (!originalRequest) {
                throw new ApiError('No request configuration available', error.response?.status);
            }

            // Handle retry logic
            originalRequest._retry = (originalRequest._retry || 0) + 1;

            if (error.response?.status === 401) {
                if (originalRequest.url === API_ENDPOINTS.AUTH.REFRESH) {
                    TokenManager.removeToken();
                    history.replace('/login');
                    throw new ApiError('Session expired', 401);
                }

                if (originalRequest._retry <= MAX_RETRIES) {
                    await sleep(RETRY_DELAY * originalRequest._retry);

                    try {
                        const newToken = await AUTH_API.refreshToken();
                        TokenManager.setToken(newToken.token);
                        originalRequest.headers = {
                            ...originalRequest.headers,
                            Authorization: `Bearer ${newToken.token}`
                        };
                        return instance(originalRequest);
                    } catch (refreshError) {
                        TokenManager.removeToken();
                        history.replace('/login');
                        throw new ApiError('Authentication failed', 401);
                    }
                }
            }

            if (error.response?.status === 429) {
                if (originalRequest._retry <= MAX_RETRIES) {
                    await sleep(RETRY_DELAY * originalRequest._retry);
                    return instance(originalRequest);
                }
            }

            const errorResponse = error.response?.data as ApiResponse<unknown>;
            throw new ApiError(
                errorResponse?.message || error.message,
                error.response?.status,
                errorResponse?.errorCode,
                error
            );
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
        } catch (error) {
            console.error('[Auth API] Logout failed:', error);
        } finally {
            TokenManager.removeToken();
        }
    },

    refreshToken: async (): Promise<AuthResponse> => {
        try {
            const { data } = await axiosInstance.post<ApiResponse<AuthResponse>>(
                API_ENDPOINTS.AUTH.REFRESH
            );

            if (!data.success || !data.data) {
                throw new ApiError(data.message || 'Token refresh failed');
            }

            TokenManager.setToken(data.data.token);
            return data.data;
        } catch (error) {
            console.error('[Auth API] Token refresh failed:', error);
            throw error instanceof ApiError ? error : new ApiError('Token refresh failed', undefined, undefined, error as Error);
        }
    },

    checkNickname: async (nickname: string): Promise<boolean> => {
        try {
            const { data } = await axiosInstance.get<ApiResponse<{ available: boolean }>>(
                API_ENDPOINTS.AUTH.CHECK_NICKNAME(nickname)
            );

            if (!data.success || data.data === undefined) {
                throw new ApiError(data.message || 'Nickname check failed');
            }

            return data.data.available;
        } catch (error) {
            console.error('[Auth API] Nickname check failed:', error);
            throw error instanceof ApiError ? error : new ApiError('Nickname check failed', undefined, undefined, error as Error);
        }
    }
} as const;

// Type declaration for axios config
declare module 'axios' {
    export interface AxiosRequestConfig {
        _retry?: number;
    }
}
