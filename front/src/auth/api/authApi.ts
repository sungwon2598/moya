import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL;

export const authApi = {
    async postLoginToken(credential: string) {
        try {
            const { data } = await axios.post(
                `${API_BASE_URL}/v1/oauth/login`,
                { credential },
                {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    withCredentials: true,
                }
            );
            return data;
        } catch (error) {
            console.error('Login Error:', error);
            return false;
        }
    },

    async getUserInfo() {
        try {
            const { data } = await axios.get<UserInfo>(
                `${API_BASE_URL}/v1/oauth/user/info`,
                {
                    withCredentials: true,
                }
            );
            return data;
        } catch (error) {
            console.error('Get User Info Error:', error);
            return null;
        }
    },
};
