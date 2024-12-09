import axios from 'axios';
import { User } from "../types/auth.types.ts"
import { BASE_URL as API_URL } from "../../../core/config/apiConfig.ts";

// axios 인스턴스 생성
const axiosInstance = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Access-Control-Allow-Origin': import.meta.env.VITE_ALLOWED_ORIGIN || '*'
    },
    withCredentials: true // credentials: 'include' 대체
});

// 요청 인터셉터 설정 - 토큰이 있다면 헤더에 추가
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

interface LoginResponse {
    success: boolean;
    data?: {
        user: User;
    };
}

export const getUserInfo = async (): Promise<User | false> => {
    try {
        const response = await axiosInstance.get('/v1/oauth/user/info');
        return response.data;
    } catch (error) {
        console.error('[API] Get user info failed:', error);
        return false;
    }
};

export const postLoginToken = async (credential: string): Promise<LoginResponse> => {
    try {
        const response = await axiosInstance.post('/v1/oauth/login', {
            credential
        });

        return { success: true, data: response.data };
    } catch (error) {
        console.error('[API] Login token verification failed:', error);
        return { success: false };
    }
};
