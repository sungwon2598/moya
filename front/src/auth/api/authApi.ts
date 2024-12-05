import {User} from "../types/auth.types.ts"
import { BASE_URL as API_URL} from "../../config/apiConfig.ts";

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

export const postLoginToken = async (credential: string): Promise<boolean> => {
    try {
        const response = await fetch(`${API_URL}/v1/oauth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include',
            body: credential
        });

        if (!response.ok) {
            return false;
        }

        return response.json();
    } catch (error) {
        console.error('[API] Login token verification failed:', error);
        return false;
    }
};
