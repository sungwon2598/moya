import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// axios 인스턴스 생성
export const api = axios.create({
    baseURL: API_URL,
    withCredentials: true, // CORS 요청에서 쿠키를 포함하기 위해 필요
    headers: {
        'Content-Type': 'application/json',
    },
});

// 응답 인터셉터 설정
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response?.status === 401) {
            // 인증 만료 시 처리 로직
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);