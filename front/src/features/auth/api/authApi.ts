import { User } from "../types/auth.types.ts"
import { BASE_URL as API_URL } from "../../../core/config/apiConfig.ts";

interface LoginResponse {
    success: boolean;
    data?: {
        user: User;
    };
}
export const getUserInfo = async (): Promise<User | false> => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/v1/oauth/user/info`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Access-Control-Allow-Origin': import.meta.env.VITE_ALLOWED_ORIGIN || '*',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            credentials: 'include',
            mode: 'cors' // CORS 모드 명시적 설정
        });

        if (!response.ok) {
            return false;
        }

        return response.json();
    } catch (error) {
        console.error('[API] Get user info failed:', error);
        return false;
    }
};

export const postLoginToken = async (credential: string): Promise<LoginResponse> => {
    try {
        const response = await fetch(`${API_URL}/v1/oauth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Access-Control-Allow-Origin': import.meta.env.VITE_ALLOWED_ORIGIN || '*'
            },
            credentials: 'include',
            mode: 'cors',
            body: JSON.stringify({ credential })
        });

        if (!response.ok) {
            return { success: false };
        }

        const data = await response.json();
        return { success: true, data };
    } catch (error) {
        console.error('[API] Login token verification failed:', error);
        return { success: false };
    }
};
