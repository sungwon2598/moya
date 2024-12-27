import axios, { AxiosInstance, AxiosError } from 'axios';
import { createBrowserHistory } from 'history';
import type { User, GoogleAuthResponse } from '../types/auth.types';

const history = createBrowserHistory();

export const BASE_URL = import.meta.env.VITE_API_URL || 'https://api.moyastudy.com';

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

// API Error class
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

// Token management
export const TokenStorage = {
    getAccessToken: () => localStorage.getItem('accessToken'),
    getRefreshToken: () => localStorage.getItem('refreshToken'),
    setTokens: (accessToken: string, refreshToken?: string) => {
        localStorage.setItem('accessToken', accessToken);
        if (refreshToken) {
            localStorage.setItem('refreshToken', refreshToken);
        }
    },
    clearTokens: () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    }
};

// Error handler
const handleApiError = (error: unknown): never => {
    if (error instanceof AxiosError) {
        const statusCode = error.response?.status;
        const errorMessage = error.response?.data?.message || error.message;
        const errorCode = error.response?.data?.errorCode;

        throw new AuthApiError(errorMessage, statusCode, errorCode);
    }

    if (error instanceof Error) {
        throw new AuthApiError(error.message);
    }

    throw new AuthApiError('An unexpected error occurred');
};

// Create axios instance
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
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    instance.interceptors.response.use(
        (response) => response,
        async (error: AxiosError) => {
            const originalRequest = error.config;

            if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
                // @ts-ignore
                originalRequest._retry = true;

                try {
                    const refreshToken = TokenStorage.getRefreshToken();
                    if (!refreshToken) throw new Error('No refresh token available');

                    const response = await instance.post<AuthResponseData>('/v1/auth/refresh', { refreshToken });
                    const { accessToken, refreshToken: newRefreshToken } = response.data;

                    TokenStorage.setTokens(accessToken, newRefreshToken);
                    if (originalRequest.headers) {
                        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                    }
                    return instance(originalRequest);
                } catch (refreshError) {
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

// Google OAuth Login
export const postGoogleAuth = async (
    authData: GoogleAuthResponse
): Promise<ApiResponse<AuthResponseData>> => {
    try {
        const response = await axiosInstance.post<AuthResponseData>(
            '/v1/oauth/login',
            authData
        );

        const { accessToken, refreshToken } = response.data;
        TokenStorage.setTokens(accessToken, refreshToken);

        return {
            success: true,
            data: response.data
        };
    } catch (error) {
        console.error('[Auth API] Google auth failed:', error);

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
        await axiosInstance.post('/v1/oauth/logout');
        TokenStorage.clearTokens();
    } catch (error) {
        console.error('[Auth API] Logout failed:', error);
        TokenStorage.clearTokens();
        throw handleApiError(error);
    }
};

// export const refreshAccessToken = async (refreshToken: string): Promise<AuthResponseData> => {
//     try {
//         const response = await axiosInstance.post<AuthResponseData>(
//             '/v1/oauth/refresh',
//             { refreshToken }
//         );
//
//         const { accessToken, refreshToken: newRefreshToken } = response.data;
//         TokenStorage.setTokens(accessToken, newRefreshToken);
//
//         return response.data;
//     } catch (error) {
//         console.error('[Auth API] Token refresh failed:', error);
//         TokenStorage.clearTokens();
//         throw handleApiError(error);
//     }
// };

// export const refreshAccessToken = async (): Promise<AuthResponseData> => {
//     try {
//         const response = await axiosInstance.post<AuthResponseData>(
//             '/v1/oauth/refresh',
//             {},  // body는 비움
//             { withCredentials: true }  // 쿠키 포함
//         );
//
//         const { accessToken, refreshToken: newRefreshToken } = response.data;
//         TokenStorage.setTokens(accessToken, newRefreshToken);
//
//         return response.data;
//     } catch (error) {
//         console.error('[Auth API] Token refresh failed:', error);
//         TokenStorage.clearTokens();
//         throw handleApiError(error);
//     }
// }; 잘되던 거

export const refreshAccessToken = async (): Promise<AuthResponseData> => {
    try {
        const response = await axiosInstance.post<AuthResponseData>(
            '/v1/oauth/refresh',
            {},
            { withCredentials: true }
        );
        // refreshToken은 쿠키에 저장되므로 TokenStorage에는 accessToken만 저장
        TokenStorage.setAccessToken(response.data.accessToken);
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
            '/api/auth/user/info'
        );
        return response.data.data!;
    } catch (error) {
        console.error('[Auth API] Get user info failed:', error);
        throw handleApiError(error);
    }
};
