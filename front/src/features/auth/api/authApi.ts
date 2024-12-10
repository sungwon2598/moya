import axios from 'axios';
import { User } from "../types/auth.types";
import { BASE_URL as API_URL } from "../../../core/config/apiConfig";

// axios 인스턴스 생성
const axiosInstance = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Access-Control-Allow-Origin': import.meta.env.VITE_ALLOWED_ORIGIN || '*'
    },
    withCredentials: true
});

// 요청 인터셉터 설정
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
export const getUserInfo = async (): Promise<User> => {
    try {
        const response = await axiosInstance.get('/v1/oauth/user/info');
        const userData: User = {
            email: response.data.email,
            nickname: response.data.nickname,
            roles: response.data.roles,
            status: response.data.status,
            profileImageUrl: response.data.profileImageUrl
        };
        return userData;
    } catch (error) {
        throw error;  // 에러 처리는 thunk에서 수행
    }
};

export const postLoginToken = async (credential: string): Promise<LoginResponse> => {
    try {
        const response = await axiosInstance.post('/v1/oauth/login', {
            credential
        });

        // 응답에서 사용자 데이터 변환
        const userData: User = {
            email: response.data.email,
            nickname: response.data.nickname,
            roles: response.data.roles,
            status: response.data.status,
            profileImageUrl: response.data.profileImageUrl
        };

        return {
            success: true,
            data: {
                user: userData
            }
        };
    } catch (error) {
        console.error('[API] Login token verification failed:', error);
        return { success: false };
    }
};
