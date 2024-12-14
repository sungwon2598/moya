import { AxiosError } from 'axios';
import { axiosInstance } from '@/core/config/apiConfig';
import { GoogleAuthResponse, User } from '../types/auth.types';

// API Base URL 설정
const API_BASE_URL = process.env.NODE_ENV === 'development'
    ? 'http://localhost:3000'
    : 'https://www.moyastudy.com';

// axiosInstance baseURL 설정
axiosInstance.defaults.baseURL = API_BASE_URL;

// API Response Types
interface ApiResponse<T> {
    success: boolean;
    data?: T;
    message?: string;
}

interface AuthResponseData {
    user: User;
    accessToken: string;
    refreshToken?: string;
}

interface TokenRefreshResponse {
    accessToken: string;
    refreshToken?: string;
}

// Error Handling
class AuthApiError extends Error {
    constructor(
        message: string,
        public statusCode?: number,
        public errorCode?: string
    ) {
        super(message);
        this.name = 'AuthApiError';
    }
}

// Token Management
const TOKEN_STORAGE = {
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

// API Error Handler
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

/**
 * Google OAuth Login
 * @param authData Google authentication data
 * @returns API response with user data and tokens
 */
export const postGoogleAuth = async (
    authData: GoogleAuthResponse
): Promise<ApiResponse<AuthResponseData>> => {
    try {
        const response = await axiosInstance.post<AuthResponseData>(
            '/v1/oauth/login',
            authData
        );

        const { accessToken, refreshToken} = response.data;
        TOKEN_STORAGE.setTokens(accessToken, refreshToken);

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

/**
 * Get current user information
 * @returns User information
 * @throws AuthApiError if request fails
 */
export const getUserInfo = async (): Promise<User> => {
    try {
        const response = await axiosInstance.get<{ user: User }>(
            '/v1/oauth/user/info'
        );
        return response.data.user;
    } catch (error) {
        console.error('[Auth API] Get user info failed:', error);
        throw handleApiError(error);
    }
};

/**
 * Refresh access token
 * @param refreshToken Current refresh token
 * @returns New access token
 * @throws AuthApiError if refresh fails
 */
export const refreshToken = async (refreshToken: string): Promise<string> => {
    try {
        const response = await axiosInstance.post<TokenRefreshResponse>(
            '/v1/oauth/refresh',
            { refreshToken }
        );

        const { accessToken, refreshToken: newRefreshToken } = response.data;
        TOKEN_STORAGE.setTokens(accessToken, newRefreshToken);

        return accessToken;
    } catch (error) {
        console.error('[Auth API] Token refresh failed:', error);
        TOKEN_STORAGE.clearTokens();
        throw handleApiError(error);
    }
};

/**
 * Logout user
 * @throws AuthApiError if logout fails
 */
export const logout = async (): Promise<void> => {
    try {
        await axiosInstance.post('/v1/oauth/logout');
        TOKEN_STORAGE.clearTokens();
    } catch (error) {
        console.error('[Auth API] Logout failed:', error);
        TOKEN_STORAGE.clearTokens(); // Always clear tokens even if API call fails
        throw handleApiError(error);
    }
};

/**
 * Check if access token exists and is not expired
 * @returns boolean indicating if user is likely logged in
 */
export const hasValidSession = (): boolean => {
    const accessToken = TOKEN_STORAGE.getAccessToken();
    if (!accessToken) return false;

    // Optional: Add JWT expiration check if tokens are JWTs
    try {
        const tokenData = JSON.parse(atob(accessToken.split('.')[1]));
        const expirationTime = tokenData.exp * 1000; // Convert to milliseconds
        return Date.now() < expirationTime;
    } catch {
        return false; // If token can't be decoded, assume session is invalid
    }
};

// Export token management utilities
export const TokenStorage = TOKEN_STORAGE;
