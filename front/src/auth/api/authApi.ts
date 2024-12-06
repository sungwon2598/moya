import { User } from "../types/auth.types.ts"
import { BASE_URL as API_URL } from "../../config/apiConfig.ts";

interface LoginResponse {
    success: boolean;
    data?: {
        user: User;
    };
}

export const getUserInfo = async (): Promise<User | false> => {
    try {
        const response = await fetch(`${API_URL}/v1/oauth/user/info`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include'
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
                'Accept': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({ credential }) // credential을 객체로 감싸서 전송
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
