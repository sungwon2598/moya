import axios, { AxiosInstance, AxiosError,  } from 'axios';
import { createBrowserHistory } from 'history';
import type { User, GoogleAuthResponse } from '../types/auth.types';

declare module 'axios' {
    export interface AxiosRequestConfig {
        _retry?: number;
    }
}

const history = createBrowserHistory();

export const BASE_URL = import.meta.env.VITE_API_URL || 'https://api.moyastudy.com';

export const OAUTH_ENDPOINTS = {
    LOGIN: '/v1/oauth/login',
    REFRESH: '/v1/oauth/refresh',
    LOGOUT: '/v1/oauth/logout',
    USER_INFO: '/v1/oauth/user/info'
} as const;

export interface ApiResponse<T> {
    success: boolean;
    data?: T;
    message?: string;
    errorCode?: string;
}

export interface AuthResponseData {
    user: User;
    accessToken: string;
    refreshToken?: string;
}

export class AuthApiError extends Error {
    constructor(
        message: string,
        public statusCode?: number,
        public errorCode?: string
    ) {
        super(message);
        this.name = 'AuthApiError';
    }
}

export const TokenStorage = {
    getAccessToken: () => localStorage.getItem('accessToken'),
    getRefreshToken: () => localStorage.getItem('refreshToken'),
    setTokens: (accessToken: string, refreshToken?: string) => {
        if (!accessToken) {
            throw new Error('Access token is required');
        }
        localStorage.setItem('accessToken', accessToken);
        if (refreshToken) {
            localStorage.setItem('refreshToken', refreshToken);
        }
        console.log('Tokens stored successfully');
    },
    clearTokens: () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        console.log('Tokens cleared');
    }
};

const handleApiError = (error: unknown): never => {
    if (error instanceof AxiosError) {
        const statusCode = error.response?.status;
        const errorMessage = error.response?.data?.message || error.message;
        const errorCode = error.response?.data?.errorCode;

        console.error('API Error:', {
            statusCode,
            errorMessage,
            errorCode,
            response: error.response?.data
        });

        throw new AuthApiError(errorMessage, statusCode, errorCode);
    }

    if (error instanceof Error) {
        console.error('General Error:', error);
        throw new AuthApiError(error.message);
    }

    throw new AuthApiError('An unexpected error occurred');
};

export const createAxiosInstance = (): AxiosInstance => {
    const instance = axios.create({
        baseURL: BASE_URL,
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        withCredentials: true
    });

    instance.interceptors.request.use(
        (config) => {
            const accessToken = TokenStorage.getAccessToken();
            if (accessToken) {
                config.headers.Authorization = `Bearer ${accessToken}`;
                console.log('Request with token:', accessToken);
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    instance.interceptors.response.use(
        (response) => response,
        async (error: AxiosError) => {
            const originalRequest = error.config;

            if (error.response?.status === 401 && originalRequest && (!originalRequest._retry || originalRequest._retry < 1)) {
                console.log('Token refresh required');
                originalRequest._retry = 1;

                try {
                    const refreshToken = TokenStorage.getRefreshToken();
                    if (!refreshToken) {
                        throw new Error('No refresh token available');
                    }

                    const response = await instance.post<AuthResponseData>(
                        OAUTH_ENDPOINTS.REFRESH,
                        { refreshToken }
                    );

                    const { accessToken, refreshToken: newRefreshToken } = response.data;
                    TokenStorage.setTokens(accessToken, newRefreshToken);

                    if (originalRequest.headers) {
                        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                    }
                    return instance(originalRequest);
                } catch (refreshError) {
                    console.error('Token refresh failed:', refreshError);
                    TokenStorage.clearTokens();
                    history.replace('/login');
                    return Promise.reject(refreshError);
                }
            }
            return Promise.reject(error);
        }
    );

    return instance;
};

export const axiosInstance = createAxiosInstance();

export const postGoogleAuth = async (
    authData: GoogleAuthResponse
): Promise<ApiResponse<AuthResponseData>> => {
    try {
        console.log('Posting Google auth data:', authData);

        const response = await axiosInstance.post<AuthResponseData>(
            OAUTH_ENDPOINTS.LOGIN,
            authData
        );

        console.log('Auth response:', response.data);

        if (!response.data.accessToken) {
            throw new Error('No access token received');
        }

        TokenStorage.setTokens(
            response.data.accessToken,
            response.data.refreshToken
        );

        return {
            success: true,
            data: response.data
        };
    } catch (error) {
        console.error('[Auth API] Google auth failed:', error);
        TokenStorage.clearTokens();

        if (error instanceof AxiosError && error.response) {
            return {
                success: false,
                message: error.response.data?.message || 'Authentication failed'
            };
        }

        return {
            success: false,
            message: 'Authentication failed'
        };
    }
};

export const logout = async (): Promise<void> => {
    try {
        await axiosInstance.post(OAUTH_ENDPOINTS.LOGOUT);
        TokenStorage.clearTokens();
    } catch (error) {
        console.error('[Auth API] Logout failed:', error);
        TokenStorage.clearTokens();
        throw handleApiError(error);
    }
};

export const refreshAccessToken = async (refreshToken: string): Promise<AuthResponseData> => {
    try {
        console.log('Refreshing token with:', refreshToken);

        const response = await axiosInstance.post<AuthResponseData>(
            OAUTH_ENDPOINTS.REFRESH,
            { refreshToken }
        );

        console.log('Refresh response:', response.data);

        const { accessToken, refreshToken: newRefreshToken } = response.data;
        TokenStorage.setTokens(accessToken, newRefreshToken);

        return response.data;
    } catch (error) {
        console.error('[Auth API] Token refresh failed:', error);
        TokenStorage.clearTokens();
        throw handleApiError(error);
    }
};

export const getUserInfo = async (): Promise<User> => {
    try {
        const response = await axiosInstance.get<ApiResponse<User>>(
            OAUTH_ENDPOINTS.USER_INFO
        );

        if (!response.data.success || !response.data.data) {
            throw new Error('Failed to get user info');
        }

        return response.data.data;
    } catch (error) {
        console.error('[Auth API] Get user info failed:', error);
        throw handleApiError(error);
    }
};
