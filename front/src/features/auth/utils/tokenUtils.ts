// features/auth/utils/tokenUtils.ts
export const TokenStorage = {
    getAccessToken: () => {
        try {
            return localStorage.getItem('accessToken');
        } catch (error) {
            console.error('Failed to get access token:', error);
            return null;
        }
    },
    getRefreshToken: () => {
        try {
            return localStorage.getItem('refreshToken');
        } catch (error) {
            console.error('Failed to get refresh token:', error);
            return null;
        }
    },
    setTokens: (accessToken: string, refreshToken?: string) => {
        try {
            if (!accessToken) {
                throw new Error('Access token is required');
            }
            localStorage.setItem('accessToken', accessToken);
            if (refreshToken) {
                localStorage.setItem('refreshToken', refreshToken);
            }
            console.log('Tokens stored successfully');
        } catch (error) {
            console.error('Failed to store tokens:', error);
            throw new Error('Failed to store tokens in localStorage');
        }
    },
    clearTokens: () => {
        try {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            console.log('Tokens cleared');
        } catch (error) {
            console.error('Failed to clear tokens:', error);
        }
    }
};
